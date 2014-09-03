package kidnox.eventbus.internal;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.element.*;
import kidnox.eventbus.internal.extraction.ClassInfoExtractor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static kidnox.eventbus.internal.Utils.*;

public class AsyncBus implements Bus {

    final Map<Object, ElementsGroup> instanceToElementsMap = newHashMap();

    final Map<Class, Set<AsyncElement>> eventTypeToSubscribersMap = newHashMap();
    final Map<Class, AsyncElement> eventTypeToProducerMap = newHashMap();

    final ClassInfoExtractor classInfoExtractor;
    final Dispatcher.Factory dispatcherFactory;

    final ErrorHandler errorHandler;
    final DeadEventHandler deadEventHandler;
    final EventLogger logger;
    final EventInterceptor interceptor;

    public AsyncBus(ClassInfoExtractor classInfoExtractor, Dispatcher.Factory factory,
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
        ClassInfo classInfo = classInfoExtractor.getClassInfo(target.getClass());
        Dispatcher dispatcher = classInfo.annotationValue == null ?
                null : dispatcherFactory.getDispatcher(classInfo.annotationValue);
        ElementsGroup elementsGroup = null;
        switch (classInfo.type) {
            case SUBSCRIBER:
                elementsGroup = new SubscriberGroup(classInfo, dispatcher);
                break;
            case PRODUCER:
                elementsGroup = new ProducerGroup(classInfo, dispatcher);
                break;
            case TASK:
                elementsGroup = new TaskGroup(classInfo, dispatcher);
                break;
            case SERVICE:
                elementsGroup = new ServiceGroup(classInfo, dispatcher);
                break;
            case NONE:
                elementsGroup = ElementsGroup.EMPTY;
                break;
        }
        if(instanceToElementsMap.put(target, elementsGroup) != null)
            throwBusException("register", target, " already registered");
        elementsGroup.registerGroup(target, this);
    }

    @Override synchronized public void unregister(Object target) {
        if(target == null) throw new NullPointerException();
        ElementsGroup elementsGroup = instanceToElementsMap.remove(target);
        if(elementsGroup == null)
            throwBusException("unregister", target, " not registered");
        else elementsGroup.unregisterGroup(this);
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

    public Set<AsyncElement> getSubscribers(Class eventType) {
        return eventTypeToSubscribersMap.get(eventType);
    }

    public Set<AsyncElement> putSubscribers(Class eventType, Set<AsyncElement> subscribers) {
        return eventTypeToSubscribersMap.put(eventType, subscribers);
    }

    public AsyncElement getProducer(Class eventType) {
        return eventTypeToProducerMap.get(eventType);
    }

    public AsyncElement putProducer(Class eventType, AsyncElement producer) {
        return eventTypeToProducerMap.put(eventType, producer);
    }

    public AsyncElement removeProducer(Class eventType) {
        return eventTypeToProducerMap.remove(eventType);
    }

    public boolean checkProducers() {
        return eventTypeToProducerMap.size() > 0;
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

    //////////////////////////////////////////////////////////////

    public Object invokeElement(AsyncElement element, Object... args) {
        try {
            Object result = element.invoke(args);
            if(result != null && !element.isValid()) {
                //unregistered subscriber return event as result so we can handle dead event
                deadEventHandler.onDeadEvent(result);
                return null;
            }
            return result;
        } catch (InvocationTargetException e) {
            if(errorHandler.handle(e.getCause(), element.target, args.length == 0 ? null : args[0])) {
                return null;
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }
    //TODO need more dispatch methods (for task and services), maybe move all to BusService or create separate dispatchers
    public void dispatch(final AsyncElement subscriber, final Object event) {
        if(subscriber.dispatcher.isDispatcherThread()) {
            Object result = invokeElement(subscriber, event);
            if(result != null) post(result);//means this is @Process method
        } else {
            subscriber.dispatcher.dispatch(new Runnable() {
                @Override public void run() {
                    Object result = invokeElement(subscriber, event);
                    if(result != null) post(result);
                }
            });
        }
    }

    public void dispatch(final AsyncElement producer, final AsyncElement subscriber) {
        if(producer.dispatcher.isDispatcherThread()) {
            Object event = produceEvent(producer, subscriber);
            if(event != null) dispatch(subscriber, event);
        } else {
            producer.dispatcher.dispatch(new Runnable() {
                @Override public void run() {
                    Object event = produceEvent(producer, subscriber);
                    if(event != null) dispatch(subscriber, event);
                }
            });
        }
    }

    public void dispatch(final AsyncElement producer) {
        if(producer.dispatcher.isDispatcherThread()) {
            Object event = produceEvent(producer, null);
            if(event != null) post(event);
        } else {
            producer.dispatcher.dispatch(new Runnable() {
                @Override public void run() {
                    Object event = produceEvent(producer, null);
                    if(event != null) post(event);
                }
            });
        }
    }
}
