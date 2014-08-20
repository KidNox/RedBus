package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.*;
import kidnox.eventbus.internal.AsyncElement;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static kidnox.eventbus.Bus.*;
import static kidnox.eventbus.internal.Utils.*;

public class BusServiceImpl implements BusService {

    final Map<Class, Set<AsyncElement>> eventTypeToSubscribersMap = newHashMap();
    final Map<Class, AsyncElement> eventTypeToProducerMap = newHashMap();

    final Map<String, EventDispatcher> dispatchersMap = newHashMap(4);

    final EventDispatcher.Factory dispatcherFactory;//TODO move to BusImpl

    final EventLogger logger;
    final DeadEventHandler deadEventHandler;
    final Interceptor interceptor;
    final ExceptionHandler exceptionHandler;

    public BusServiceImpl(EventDispatcher.Factory factory, ExceptionHandler exceptionHandler,
                          DeadEventHandler deadEventHandler, EventLogger logger, Interceptor interceptor) {
        this.dispatcherFactory = factory;
        this.exceptionHandler = exceptionHandler;
        this.deadEventHandler = deadEventHandler;
        this.logger = logger;
        this.interceptor = interceptor;
    }

    @Override public List<AsyncElement> registerSubscribers(Object target, ClassInfo classInfo) {
        final boolean checkProducers = eventTypeToProducerMap.size() > 0;
        List<AsyncElement>subscribers = new LinkedList<AsyncElement>();

        for(ElementInfo entry : classInfo.elements) {
            final AsyncElement subscriber = getEventSubscriber(target, entry,
                    getDispatcher(classInfo.annotationValue));
            subscribers.add(subscriber);
            if(checkProducers) {
                AsyncElement producer = eventTypeToProducerMap.get(subscriber.eventType);
                if(producer != null) {
                    dispatch(producer, subscriber);
                }
            }
            Set<AsyncElement> set = eventTypeToSubscribersMap.get(subscriber.eventType);
            if (set == null) {
                set = newHashSet(2);
                eventTypeToSubscribersMap.put(subscriber.eventType, set);
            }
            set.add(subscriber);
        }
        return subscribers;
    }

    @Override public List<AsyncElement> registerProducers(Object target, ClassInfo classInfo) {
        List<AsyncElement> producers = new LinkedList<AsyncElement>();

        for(ElementInfo entry : classInfo.elements) {
            final AsyncElement producer = getEventProducer(target, entry);
            if(eventTypeToProducerMap.put(producer.eventType, producer) != null) {
                throwIllegalStateException("register", target, " producer for event "
                        + producer.eventType + " already registered");
            }
            producers.add(producer);
            final Set<AsyncElement> subscribers = eventTypeToSubscribersMap.get(producer.eventType);
            if(notEmpty(subscribers)) {
                dispatch(producer);
            }
        }
        return producers;
    }

    @Override public void unregisterSubscribers(List<AsyncElement> subscribers) {
        for (AsyncElement subscriber : subscribers) {
            eventTypeToSubscribersMap.get(subscriber.eventType).remove(subscriber);
            subscriber.onUnregister();
        }
    }

    @Override public void unregisterProducers(List<AsyncElement> producers) {
        for(AsyncElement producer : producers) {
            eventTypeToProducerMap.remove(producer.eventType);
        }
    }

    @Override public void post(Object event) {
        Set<AsyncElement> set = eventTypeToSubscribersMap.get(event.getClass());
        logger.logEvent(event, set, POST);
        if (notEmpty(set)) {
            if(interceptor.intercept(event)) {
                logger.logEvent(event, set, INTERCEPT);
                return;
            }
            for (AsyncElement subscriber : set) {
                dispatch(subscriber, event);
            }
        } else {
            deadEventHandler.onDeadEvent(event);
        }
    }

    Object produceEvent(AsyncElement producer, Object target) {
        final Object event = invokeElement(producer);
        if(event != null && interceptor.intercept(event)) {
            logger.logEvent(event, target, INTERCEPT);
            return null;
        }
        logger.logEvent(event, target, PRODUCE);
        return event;
    }

    EventDispatcher getDispatcher(String dispatcherName) {
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

    AsyncElement getEventSubscriber(Object target, ElementInfo elementInfo, EventDispatcher dispatcher) {
        return new AsyncElement(elementInfo, target, dispatcher);
    }

    AsyncElement getEventProducer(Object target, ElementInfo elementInfo) {
        return new AsyncElement(elementInfo, target, null);
    }

    Object invokeElement(AsyncElement element, Object... args) {
        try {
            Object result = element.invoke(args);
            //this is unregistered subscriber, so we can handle dead event
            if(result != null && !element.isValid()) {
                deadEventHandler.onDeadEvent(result);
                return null;
            }
            return result;
        } catch (InvocationTargetException e) {
            if(exceptionHandler != null &&
                    exceptionHandler.handle(e.getCause(), element.target, args.length == 0 ? null : args[0])) {
                return null;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    @Override public void dispatch(final AsyncElement subscriber, final Object event) {
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

    @Override public void dispatch(AsyncElement producer, AsyncElement subscriber) {
        Object event = produceEvent(producer, subscriber);
        if(event != null) dispatch(subscriber, event);
    }

    @Override public void dispatch(AsyncElement producer) {
        Object event = produceEvent(producer, null);
        if(event != null) post(event);//TODO
    }
}


