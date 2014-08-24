package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.*;
import kidnox.eventbus.internal.AsyncElement;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static kidnox.eventbus.internal.Utils.*;

public class AsyncBus implements Bus {

    final Map<Object, List<AsyncElement>> instanceToSubscribersMap = newHashMap();
    final Map<Object, List<AsyncElement>> instanceToProducersMap = newHashMap(4);

    final Map<Class, Set<AsyncElement>> eventTypeToSubscribersMap = newHashMap();
    final Map<Class, AsyncElement> eventTypeToProducerMap = newHashMap();

    final Map<String, EventDispatcher> dispatchersMap = newHashMap(4);

    final ClassInfoExtractor classInfoExtractor;
    final EventDispatcher.Factory dispatcherFactory;

    final EventLogger logger;
    final DeadEventHandler deadEventHandler;
    final EventInterceptor interceptor;
    final ErrorHandler errorHandler;

    public AsyncBus(ClassInfoExtractor classInfoExtractor, EventDispatcher.Factory factory,
                    ErrorHandler errorHandler, DeadEventHandler deadEventHandler,
                    EventLogger logger, EventInterceptor interceptor) {
        this.classInfoExtractor = classInfoExtractor;
        this.dispatcherFactory = factory;
        this.errorHandler = errorHandler;
        this.deadEventHandler = deadEventHandler;
        this.logger = logger;
        this.interceptor = interceptor;
    }

    @Override synchronized public void register(Object target) {
        final ClassInfo classInfo = classInfoExtractor.getClassInfo(target.getClass());
        switch (classInfo.type) {
            case SUBSCRIBER:
                registerSubscriber(target, classInfo);
                break;
            case PRODUCER:
                registerProducer(target, classInfo);
                break;
            case SERVICE:
                registerService(target, classInfo);
                break;
            case NONE:
                if(instanceToSubscribersMap.put(target, Collections.<AsyncElement>emptyList()) != null)
                    throwIllegalStateException("register", target, " already registered");
                break;
        }
    }

    @Override synchronized public void unregister(Object target) {
        final Class targetClass = target.getClass();
        final ClassInfo classInfo = classInfoExtractor.getClassInfo(targetClass);
        switch (classInfo.type) {
            case SUBSCRIBER:
                unregisterSubscribers(target);
                break;
            case PRODUCER:
                unregisterProducers(target);
                break;
            case SERVICE:
                unregisterService(target);
                break;
            case NONE:
                if(instanceToSubscribersMap.remove(target) == null)
                    throwIllegalStateException("unregister", target, " not registered");
                break;
        }
    }

    @Override synchronized public void post(Object event) {
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

    void registerSubscriber(Object target, ClassInfo classInfo) {
        List<AsyncElement> subscribers;
        if(classInfo.isEmpty()) {
            subscribers = Collections.emptyList();
        } else {
            subscribers = registerSubscribers(target, classInfo);
        }
        if (instanceToSubscribersMap.put(target, subscribers) != null)
            throwIllegalStateException("register", target, " already registered");
    }

    void registerProducer(Object target, ClassInfo classInfo) {
        List<AsyncElement> producers;
        if(classInfo.isEmpty()) {
            producers = Collections.emptyList();
        } else {
            producers = registerProducers(target, classInfo);
        }
        if (instanceToProducersMap.put(target, producers) != null)
            throwIllegalStateException("register", target, " already registered");
    }

    void registerService(Object target, ClassInfo classInfo) {
        //TODO need map for targets and factory for instances
    }

    void unregisterSubscribers(Object target) {
        final List<AsyncElement> subscribers = instanceToSubscribersMap.remove(target);
        if (subscribers == null) throwIllegalStateException("unregister", target, " not registered");
        else if(!subscribers.isEmpty()){
            for (AsyncElement subscriber : subscribers) {
                eventTypeToSubscribersMap.get(subscriber.eventType).remove(subscriber);
                subscriber.onUnregister();
            }
        }
    }

    void unregisterProducers(Object target) {
        final List<AsyncElement> producers = instanceToProducersMap.remove(target);
        if (producers == null) throwIllegalStateException("unregister", target, " not registered");
        else if(!producers.isEmpty()) {
            for(AsyncElement producer : producers) {
                eventTypeToProducerMap.remove(producer.eventType);
                producer.onUnregister();
            }
        }
    }

    void unregisterService(Object target) {
        //TODO only remove instances from map (we need not synchronized version of the register method maybe)
    }

    List<AsyncElement> registerSubscribers(Object target, ClassInfo classInfo) {
        final boolean checkProducers = eventTypeToProducerMap.size() > 0;
        List<AsyncElement>subscribers = new LinkedList<AsyncElement>();

        for(ElementInfo entry : classInfo.elements) {
            final AsyncElement subscriber = new AsyncElement(target, entry, getDispatcher(classInfo.annotationValue));
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

    List<AsyncElement> registerProducers(Object target, ClassInfo classInfo) {
        List<AsyncElement> producers = new LinkedList<AsyncElement>();
        for(ElementInfo entry : classInfo.elements) {
            final AsyncElement producer = new AsyncElement(target, entry, getDispatcher(classInfo.annotationValue));
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

    Object produceEvent(AsyncElement producer, Object target) {
        final Object event = invokeElement(producer);
        if(event != null && interceptor.intercept(event)) {
            logger.logEvent(event, target, INTERCEPT);
            return null;
        }
        logger.logEvent(event, target, PRODUCE);
        return event;
    }

    Object invokeElement(AsyncElement element, Object... args) {
        try {
            Object result = element.invoke(args);
            if(result != null && !element.isValid()) {
                //unregistered subscriber return event as result so we can handle dead event
                deadEventHandler.onDeadEvent(result);
                return null;
            }
            return result;
        } catch (InvocationTargetException e) {
            if(errorHandler != null &&
                    errorHandler.handle(e.getCause(), element.target, args.length == 0 ? null : args[0])) {
                return null;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    void dispatch(final AsyncElement subscriber, final Object event) {
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

    void dispatch(final AsyncElement producer, final AsyncElement subscriber) {
        if(producer.eventDispatcher.isDispatcherThread()) {
            Object event = produceEvent(producer, subscriber);
            if(event != null) dispatch(subscriber, event);
        } else {
            producer.eventDispatcher.dispatch(new Runnable() {
                @Override
                public void run() {
                    Object event = produceEvent(producer, subscriber);
                    if(event != null) dispatch(subscriber, event);
                }
            });
        }
    }

    void dispatch(final AsyncElement producer) {
        if(producer.eventDispatcher.isDispatcherThread()) {
            Object event = produceEvent(producer, null);
            if(event != null) post(event);
        } else {
            producer.eventDispatcher.dispatch(new Runnable() {
                @Override
                public void run() {
                    Object event = produceEvent(producer, null);
                    if(event != null) post(event);
                }
            });
        }
    }
}
