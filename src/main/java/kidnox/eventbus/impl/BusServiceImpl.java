package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.elements.EventProducer;
import kidnox.eventbus.elements.EventSubscriber;
import kidnox.eventbus.internal.BusService;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.InternalFactory;

import java.lang.reflect.Method;
import java.util.*;

import static kidnox.eventbus.Bus.*;
import static kidnox.eventbus.internal.Utils.*;

public class BusServiceImpl implements BusService {

    final Map<Class, Set<EventSubscriber>> eventTypeToSubscribersMap = newHashMap();
    final Map<Class, EventProducer> eventTypeToProducerMap = newHashMap();

    final Map<String, EventDispatcher> dispatchersMap = newHashMap(4);

    final EventDispatcher.Factory dispatcherFactory;

    final EventLogger logger;
    final DeadEventHandler deadEventHandler;
    final Interceptor interceptor;
    final ExceptionHandler exceptionHandler;

    public BusServiceImpl(EventDispatcher.Factory factory, EventLogger logger, DeadEventHandler deadEventHandler,
                          Interceptor interceptor, ExceptionHandler exceptionHandler) {
        this.dispatcherFactory = factory == null ? InternalFactory.createDefaultEventDispatcherFactory() : factory;
        this.logger = logger == null ? InternalFactory.getStubLogger() : logger;
        this.deadEventHandler = deadEventHandler;
        this.interceptor = interceptor;
        this.exceptionHandler = exceptionHandler;
    }

    @Override public List<EventSubscriber> registerSubscribers(Object target, ClassInfo classInfo) {
        final boolean checkProducers = eventTypeToProducerMap.size() > 0;
        List<EventSubscriber>subscribers = new LinkedList<EventSubscriber>();
        for(Map.Entry<Class, Method> entry : classInfo.typedMethodsMap.entrySet()) {
            final EventSubscriber subscriber = getEventSubscriber(entry.getKey(), target,
                    entry.getValue(), getDispatcher(classInfo.annotationValue));
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
                set = newHashSet(2);
                eventTypeToSubscribersMap.put(subscriber.eventClass, set);
            }
            set.add(subscriber);
        }
        return subscribers;
    }

    @Override public List<EventProducer> registerProducers(Object target, ClassInfo classInfo) {
        List<EventProducer> producers = new LinkedList<EventProducer>();
        for(Map.Entry<Class, Method> entry : classInfo.typedMethodsMap.entrySet()) {
            final EventProducer producer = getEventProducer(entry.getKey(), target, entry.getValue());
            if(eventTypeToProducerMap.put(producer.eventClass, producer) != null) {
                throwIllegalStateException("register", target, " producer for event "
                        + producer.eventClass + " already registered");
            }
            producers.add(producer);
            final Set<EventSubscriber> subscribers = eventTypeToSubscribersMap.get(producer.eventClass);
            if(notEmpty(subscribers)) {
                final Object event = produceEvent(producer, subscribers);
                if(event == null) continue;
                for(EventSubscriber subscriber : subscribers) {
                    subscriber.receive(event);
                }
            }
        }
        return producers;
    }

    @Override public void unregisterSubscribers(List<EventSubscriber> subscribers) {
        for (EventSubscriber subscriber : subscribers) {
            eventTypeToSubscribersMap.get(subscriber.eventClass).remove(subscriber);
            subscriber.onUnregister();
        }
    }

    @Override public void unregisterProducers(List<EventProducer> producers) {
        for(EventProducer producer : producers) {
            eventTypeToProducerMap.remove(producer.eventClass);
        }
    }

    @Override public void post(Object event) {
        Set<EventSubscriber> set = eventTypeToSubscribersMap.get(event.getClass());
        if(interceptor != null && interceptor.intercept(event)) {
            logger.logEvent(event, set, INTERCEPT);//TODO must not be called when no subscribers
            return;
        }
        logger.logEvent(event, set, POST);
        if (notEmpty(set)) {
            for (EventSubscriber subscriber : set) {
                subscriber.receive(event);
            }
        } else if(deadEventHandler != null) {
            deadEventHandler.onDeadEvent(event);
        }
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

    protected EventDispatcher getDispatcher(String dispatcherName) {
        EventDispatcher dispatcher = dispatchersMap.get(dispatcherName);
        if(dispatcher == null) {
            dispatcher = dispatcherFactory.getDispatcher(dispatcherName);
            if(dispatcher == null) {
                if(dispatcherName.isEmpty()) {
                    dispatcher = InternalFactory.CURRENT_THREAD_DISPATCHER;
                } else {
                    throw new IllegalArgumentException("Dispatcher ["+dispatcherName+"] not found");
                }
            }
            dispatchersMap.put(dispatcherName, dispatcher);
        }
        return dispatcher;
    }

    EventSubscriber getEventSubscriber(Class event, Object target, Method method, EventDispatcher dispatcher) {
        return new EventSubscriber(event, target, method, dispatcher, exceptionHandler);
    }

    EventProducer getEventProducer(Class event, Object target, Method method) {
        return new EventProducer(event, target, method, exceptionHandler);
    }

//    @Override public void dispatch(EventSubscriber subscriber, Object event) {
//
//    }
//
//    @Override public void dispatch(EventProducer producer, EventSubscriber subscriber) {
//
//    }
//
//    @Override public void dispatch(EventProducer producer, Bus bus) {
//
//    }
}


