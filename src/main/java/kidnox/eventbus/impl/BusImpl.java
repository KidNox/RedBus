package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.utils.Utils;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Map.Entry;

public class BusImpl implements Bus {

    public static final String POST = "post";
    public static final String PRODUCE = "produce";

    final Map<Object, List<EventSubscriber>> instanceToSubscribersMap = new HashMap<Object, List<EventSubscriber>>();
    final Map<Class, Set<EventSubscriber>> eventTypeToSubscribersMap = new HashMap<Class, Set<EventSubscriber>>();

    final Map<Object, Set<EventProducer>> instanceToProducersMap = new HashMap<Object, Set<EventProducer>>();
    final Map<Class, EventProducer> eventTypeToProducerMap = new HashMap<Class, EventProducer>();

    final String name;
    final EventLogger logger;
    final DeadEventHandler deadEventHandler;

    final ClassInfoExtractor classInfoExtractor;

    public BusImpl(String name, ClassInfoExtractor classInfoExtractor,
                   EventLogger logger, DeadEventHandler deadEventHandler) {
        this.name = name;
        this.logger = logger;
        this.deadEventHandler = deadEventHandler;
        this.classInfoExtractor = classInfoExtractor;
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
                    throwRuntimeException("register", target, " already registered");
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
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
                    throwRuntimeException("unregister", target, " not registered");
                break;
        }
    }

    @Override public void post(Object event) {
        Set<EventSubscriber> set = eventTypeToSubscribersMap.get(event.getClass());
        logEvent(event, set, POST);
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
                        entry.getValue(), classSubscribers.dispatcher);
                subscribers.add(subscriber);
                if(checkProducers) {
                    final EventProducer producer = eventTypeToProducerMap.get(subscriber.eventClass);
                    if(producer != null) produce(producer, subscriber);
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
            throwRuntimeException("register", target, " already registered");
    }

    void registerProducer(Object target, Class targetClass) {
        final ClassProducers classProducers = classInfoExtractor.getClassProducers(targetClass);
        Set<EventProducer> producers;
        if(ClassProducers.isNullOrEmpty(classProducers)) {
            producers = Collections.emptySet();
        } else {
            producers = new HashSet<EventProducer>(classProducers.typedMethodsMap.size());
            for(Entry<Class, Method> entry : classProducers.typedMethodsMap.entrySet()) {
                final EventProducer producer = new EventProducer(entry.getKey(), target, entry.getValue());
                if(eventTypeToProducerMap.put(producer.eventClass, producer) != null) {
                    throwRuntimeException("register", target, " producer for event "
                            + producer.eventClass +" already registered");
                }
                producers.add(producer);
                final Set<EventSubscriber> subscribers = eventTypeToSubscribersMap.get(producer.eventClass);
                if(Utils.notEmpty(subscribers)) {
                    for(EventSubscriber subscriber : subscribers) {
                        produce(producer, subscriber);
                    }
                }
            }
        }
        if (instanceToProducersMap.put(target, producers) != null)
            throwRuntimeException("register", target, " already registered");
    }

    void unregisterSubscribers(Object target) {
        final List<EventSubscriber> subscribers = instanceToSubscribersMap.remove(target);
        if (subscribers == null) throwRuntimeException("unregister", target, " not registered");
        else if(!subscribers.isEmpty()){
            for (EventSubscriber subscriber : subscribers) {
                eventTypeToSubscribersMap.get(subscriber.eventClass).remove(subscriber);
                subscriber.onUnregister();
            }
        }
    }

    void unregisterProducers(Object target) {
        final Set<EventProducer> producers = instanceToProducersMap.remove(target);
        if (producers == null) throwRuntimeException("unregister", target, " not registered");
        else if(!producers.isEmpty()) {
            for(EventProducer producer : producers) {
                eventTypeToProducerMap.remove(producer.eventClass);
            }
        }
    }

    EventSubscriber getEventSubscriber(Class event, Object target, Method method, Dispatcher dispatcher) {
        return new EventSubscriber(event, target, method, dispatcher);
    }

    void produce(EventProducer producer, EventSubscriber subscriber) {
        Object event = producer.invoke(null);
        logEvent(event, subscriber, PRODUCE);
        subscriber.receive(event);
    }

    void logEvent(Object event, Object element, String what) {
        if(logger != null) logger.logEvent(event, element, what);
    }

    protected void throwRuntimeException(String action, Object cause, String message) {
        throw new IllegalStateException(action + " was failed in " + toString() + ", " + cause + message);
    }

    @Override public String toString() {
        return "Bus[" + name + ']';
    }

}
