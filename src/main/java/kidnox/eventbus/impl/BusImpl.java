package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.elements.ClassProducers;
import kidnox.eventbus.elements.ClassSubscribers;
import kidnox.eventbus.elements.EventProducer;
import kidnox.eventbus.elements.EventSubscriber;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.util.Utils;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Map.Entry;

public class BusImpl implements Bus {

    public static final String POST         = "post";
    public static final String PRODUCE      = "produce";
    public static final String INTERCEPT    = "intercept";

    final Map<Object, List<EventSubscriber>> instanceToSubscribersMap = new HashMap<Object, List<EventSubscriber>>();
    final Map<Class, Set<EventSubscriber>> eventTypeToSubscribersMap = new HashMap<Class, Set<EventSubscriber>>();

    final Map<Object, Set<EventProducer>> instanceToProducersMap = new HashMap<Object, Set<EventProducer>>();
    final Map<Class, EventProducer> eventTypeToProducerMap = new HashMap<Class, EventProducer>();

    final String name;
    final EventLogger logger;
    final DeadEventHandler deadEventHandler;
    final Interceptor interceptor;
    final ExceptionHandler exceptionHandler;

    final ClassInfoExtractor classInfoExtractor;

    public BusImpl(String name, ClassInfoExtractor classInfoExtractor, EventLogger logger,
                   DeadEventHandler deadEventHandler, Interceptor interceptor, ExceptionHandler exceptionHandler) {
        this.name = name;
        this.logger = logger == null ? InternalFactory.getStubLogger() : logger;
        this.deadEventHandler = deadEventHandler;
        this.interceptor = interceptor;
        this.classInfoExtractor = classInfoExtractor;
        this.exceptionHandler = exceptionHandler;
    }

    @Override public void register(Object target) {
        final Class targetClass = target.getClass();
        switch (classInfoExtractor.getTypeOf(targetClass)) {
            case SUBSCRIBER:
                registerSubscriber(target, targetClass);
                break;
            case PRODUCER:
                registerProducer(target, targetClass);
                break;
            case NONE:
                if(instanceToSubscribersMap.put(target, Collections.<EventSubscriber>emptyList()) != null)
                    throwIllegalStateException("register", target, " already registered");
                break;
        }
    }

    @Override public void unregister(Object target) {
        final Class targetClass = target.getClass();
        switch (classInfoExtractor.getTypeOf(targetClass)) {
            case SUBSCRIBER:
                unregisterSubscribers(target);
                break;
            case PRODUCER:
                unregisterProducers(target);
                break;
            case NONE:
                if(instanceToSubscribersMap.remove(target) == null)
                    throwIllegalStateException("unregister", target, " not registered");
                break;
        }
    }

    @Override public void post(Object event) {
        Set<EventSubscriber> set = eventTypeToSubscribersMap.get(event.getClass());
        if(interceptor != null && interceptor.intercept(event)) {
            logger.logEvent(event, set, INTERCEPT);
            return;
        }
        logger.logEvent(event, set, POST);
        if (Utils.notEmpty(set)) {
            for (EventSubscriber subscriber : set) {
                subscriber.receive(event);
            }
        } else if(deadEventHandler != null) {
            deadEventHandler.onDeadEvent(event);
        }
    }

    void registerSubscriber(Object target, Class targetClass) {
        final ClassSubscribers classSubscribers = classInfoExtractor.getClassSubscribers(targetClass);
        List<EventSubscriber> subscribers;
        if(ClassSubscribers.isNullOrEmpty(classSubscribers)) {
            subscribers = Collections.emptyList();
        } else {
            final boolean checkProducers = instanceToProducersMap.size() > 0;
            subscribers = new LinkedList<EventSubscriber>();
            for(Entry<Class, Method> entry : classSubscribers.typedMethodsMap.entrySet()) {
                final EventSubscriber subscriber = getEventSubscriber(entry.getKey(), target,
                        entry.getValue(), classSubscribers.eventDispatcher);
                subscribers.add(subscriber);
                if(checkProducers) {
                    EventProducer producer = eventTypeToProducerMap.get(subscriber.eventClass);
                    if(producer != null) {
                        final Object event = produceEvent(producer, subscriber);
                        if(event != null) {
                            subscriber.receive(event);
                        }
                    }
                }
                Set<EventSubscriber> set = eventTypeToSubscribersMap.get(subscriber.eventClass);
                if (set == null) {
                    set = new HashSet<EventSubscriber>(2);
                    eventTypeToSubscribersMap.put(subscriber.eventClass, set);
                }
                set.add(subscriber);
            }
        }
        if (instanceToSubscribersMap.put(target, subscribers) != null)
            throwIllegalStateException("register", target, " already registered");
    }

    void registerProducer(Object target, Class targetClass) {
        final ClassProducers classProducers = classInfoExtractor.getClassProducers(targetClass);
        Set<EventProducer> producers;
        if(ClassProducers.isNullOrEmpty(classProducers)) {
            producers = Collections.emptySet();
        } else {
            producers = new HashSet<EventProducer>(classProducers.typedMethodsMap.size());
            for(Entry<Class, Method> entry : classProducers.typedMethodsMap.entrySet()) {
                final EventProducer producer = getEventProducer(entry.getKey(), target, entry.getValue());
                if(eventTypeToProducerMap.put(producer.eventClass, producer) != null) {
                    throwIllegalStateException("register", target, " producer for event "
                            + producer.eventClass + " already registered");
                }
                producers.add(producer);
                final Set<EventSubscriber> subscribers = eventTypeToSubscribersMap.get(producer.eventClass);
                if(Utils.notEmpty(subscribers)) {
                    final Object event = produceEvent(producer, subscribers);
                    if(event == null) continue;
                    for(EventSubscriber subscriber : subscribers) {
                        subscriber.receive(event);
                    }
                }
            }
        }
        if (instanceToProducersMap.put(target, producers) != null)
            throwIllegalStateException("register", target, " already registered");
    }

    void unregisterSubscribers(Object target) {
        final List<EventSubscriber> subscribers = instanceToSubscribersMap.remove(target);
        if (subscribers == null) throwIllegalStateException("unregister", target, " not registered");
        else if(!subscribers.isEmpty()){
            for (EventSubscriber subscriber : subscribers) {
                eventTypeToSubscribersMap.get(subscriber.eventClass).remove(subscriber);
                subscriber.onUnregister();
            }
        }
    }

    void unregisterProducers(Object target) {
        final Set<EventProducer> producers = instanceToProducersMap.remove(target);
        if (producers == null) throwIllegalStateException("unregister", target, " not registered");
        else if(!producers.isEmpty()) {
            for(EventProducer producer : producers) {
                eventTypeToProducerMap.remove(producer.eventClass);
            }
        }
    }

    EventSubscriber getEventSubscriber(Class event, Object target, Method method, EventDispatcher dispatcher) {
        return new EventSubscriber(event, target, method, dispatcher, exceptionHandler);
    }

    EventProducer getEventProducer(Class event, Object target, Method method) {
        return new EventProducer(event, target, method, exceptionHandler);
    }

    Object produceEvent(EventProducer producer, Object target) {
        final Object event = producer.invoke(null);
        if(event != null && interceptor != null && interceptor.intercept(event)) {
            logger.logEvent(event, target, INTERCEPT);
            return null;
        }
        logger.logEvent(event, target, PRODUCE);
        return event;
    }

    protected void throwIllegalStateException(String action, Object cause, String message) {
        throw new IllegalStateException(action + " was failed in " + toString() + ", " + cause + message);
    }

    @Override public String toString() {
        return "Bus[" + name + ']';
    }

}
