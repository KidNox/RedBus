package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.Element;
import kidnox.eventbus.internal.EventProducer;
import kidnox.eventbus.internal.EventSubscriber;
import kidnox.eventbus.internal.BusService;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ElementInfo;
import kidnox.eventbus.internal.InternalFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static kidnox.eventbus.Bus.*;
import static kidnox.eventbus.internal.Utils.*;

public class BusServiceImpl implements BusService {

    final Map<Class, Set<EventSubscriber>> eventTypeToSubscribersMap = newHashMap();
    final Map<Class, EventProducer> eventTypeToProducerMap = newHashMap();

    final Map<String, EventDispatcher> dispatchersMap = newHashMap(4);

    final EventDispatcher.Factory dispatcherFactory;//TODO move to BusImpl

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

        for(ElementInfo entry : classInfo.elements) {
            final EventSubscriber subscriber = getEventSubscriber(target, entry,
                    getDispatcher(classInfo.annotationValue));
            subscribers.add(subscriber);
            if(checkProducers) {
                EventProducer producer = eventTypeToProducerMap.get(subscriber.eventClass);
                if(producer != null) {
                    dispatch(producer, subscriber);
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

        for(ElementInfo entry : classInfo.elements) {
            final EventProducer producer = getEventProducer(target, entry);
            if(eventTypeToProducerMap.put(producer.eventClass, producer) != null) {
                throwIllegalStateException("register", target, " producer for event "
                        + producer.eventClass + " already registered");
            }
            producers.add(producer);
            final Set<EventSubscriber> subscribers = eventTypeToSubscribersMap.get(producer.eventClass);
            if(notEmpty(subscribers)) {
                dispatch(producer);
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
        logger.logEvent(event, set, POST);
        if (notEmpty(set)) {
            if(interceptor != null && interceptor.intercept(event)) {
                logger.logEvent(event, set, INTERCEPT);
                return;
            }
            for (EventSubscriber subscriber : set) {
                dispatch(subscriber, event);
            }
        } else if(deadEventHandler != null) {
            deadEventHandler.onDeadEvent(event);
        }
    }

    Object produceEvent(EventProducer producer, Object target) {
        final Object event = invokeElement(producer);
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
                    throw new IllegalArgumentException("Dispatcher["+dispatcherName+"] not found");
                }
            }
            dispatchersMap.put(dispatcherName, dispatcher);
        }
        return dispatcher;
    }

    EventSubscriber getEventSubscriber(Object target, ElementInfo elementInfo, EventDispatcher dispatcher) {
        return new EventSubscriber(elementInfo.eventType, target, elementInfo.method, dispatcher);
    }

    EventProducer getEventProducer(Object target, ElementInfo elementInfo) {
        return new EventProducer(elementInfo.eventType, target, elementInfo.method);
    }

    protected Object invokeElement(Element element, Object... args) {
        try {
            return element.invoke(args);
        } catch (InvocationTargetException e) {
            if(exceptionHandler != null &&
                    exceptionHandler.handle(e.getCause(), element.target, args.length == 0 ? null : args[0])) {
                return null;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    @Override public void dispatch(final EventSubscriber subscriber, final Object event) {
        if(subscriber.eventDispatcher.isDispatcherThread()) {
            invokeElement(subscriber, event);
        } else {
            subscriber.eventDispatcher.dispatch(new Runnable() {
                @Override public void run() {
                    invokeElement(subscriber, event);
                }
            });
        }
    }

    @Override public void dispatch(EventProducer producer, EventSubscriber subscriber) {
        Object event = produceEvent(producer, subscriber);
        if(event != null) dispatch(subscriber, event);
    }

    @Override public void dispatch(EventProducer producer) {
        Object event = produceEvent(producer, null);
        if(event != null) post(event);//TODO
    }
}


