package kidnox.eventbus.internal;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.element.AsyncElement;
import kidnox.eventbus.internal.element.ElementInfo;
import kidnox.eventbus.internal.element.ProducerGroup;
import kidnox.eventbus.internal.element.SubscriberGroup;
import kidnox.eventbus.internal.extraction.ClassInfoExtractor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static kidnox.eventbus.internal.Utils.*;

public class AsyncBus implements Bus {

    final Map<Object, ElementsGroup> instanceToElementsMap = newHashMap();

    final Map<Class, Set<AsyncElement>> eventTypeToSubscribersMap = newHashMap();
    final Map<Class, AsyncElement> eventTypeToProducerMap = newHashMap();

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
        ElementsGroup elementsGroup = null;
        switch (classInfo.type) {
            case SUBSCRIBER:
                elementsGroup = registerSubscriber(target, classInfo);
                break;
            case PRODUCER:
                elementsGroup = registerProducer(target, classInfo);
                break;
            case SERVICE:
                elementsGroup = registerService(target, classInfo);
                break;
            case NONE:
                elementsGroup = ElementsGroup.EMPTY;
                break;
        }
        if(instanceToElementsMap.put(target, elementsGroup) != null)
            throwBusException("register", target, " already registered");
        elementsGroup.registerGroup(target);
    }

    @Override synchronized public void unregister(Object target) {
        if(target == null) throw new NullPointerException();
        ElementsGroup elementsGroup = instanceToElementsMap.remove(target);
        if(elementsGroup == null) throwBusException("unregister", target, " not registered");
        else elementsGroup.unregisterGroup();
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

    ElementsGroup registerSubscriber(Object target, ClassInfo classInfo) {
        final boolean checkProducers = eventTypeToProducerMap.size() > 0;
        final List<AsyncElement> subscribers = new LinkedList<AsyncElement>();
        for(ElementInfo entry : classInfo.elements) {
            final AsyncElement subscriber = new AsyncElement(target, entry,
                    dispatcherFactory.getDispatcher(classInfo.annotationValue));
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
        return new SubscriberGroup(subscribers, eventTypeToSubscribersMap);
    }

    ElementsGroup registerProducer(Object target, ClassInfo classInfo) {
        List<AsyncElement> producers = new LinkedList<AsyncElement>();
        for(ElementInfo entry : classInfo.elements) {
            final AsyncElement producer = new AsyncElement(target, entry,
                    dispatcherFactory.getDispatcher(classInfo.annotationValue));
            if(eventTypeToProducerMap.put(producer.eventType, producer) != null) {
                throwBusException("register", target, " producer for event "
                        + producer.eventType + " already registered");
            }
            producers.add(producer);
            final Set<AsyncElement> subscribers = eventTypeToSubscribersMap.get(producer.eventType);
            if(notEmpty(subscribers)) {
                dispatch(producer);
            }
        }
        return new ProducerGroup(producers, eventTypeToProducerMap);
    }

    ElementsGroup registerService(Object target, ClassInfo classInfo) {

        return null;
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
            Object result = invokeElement(subscriber, event);
            if(result != null) post(result);//means this is @Process method
        } else {
            subscriber.eventDispatcher.dispatch(new Runnable() {
                @Override public void run() {
                    Object result = invokeElement(subscriber, event);
                    if(result != null) post(result);
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
                @Override public void run() {
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
                @Override public void run() {
                    Object event = produceEvent(producer, null);
                    if(event != null) post(event);
                }
            });
        }
    }
}
