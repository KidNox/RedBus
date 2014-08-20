package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.elements.EventProducer;
import kidnox.eventbus.elements.EventSubscriber;
import kidnox.eventbus.internal.BusService;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ClassInfoExtractor;

import java.util.*;

import static kidnox.eventbus.internal.Utils.*;

public class AsyncBus implements Bus {

    final Map<Object, List<EventSubscriber>> instanceToSubscribersMap = newHashMap();
    final Map<Object, List<EventProducer>> instanceToProducersMap = newHashMap();

    final BusService busService;
    final ClassInfoExtractor classInfoExtractor;

    public AsyncBus(BusService busService, ClassInfoExtractor classInfoExtractor) {
        this.busService = busService;
        this.classInfoExtractor = classInfoExtractor;
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
            case NONE:
                if(instanceToSubscribersMap.put(target, Collections.<EventSubscriber>emptyList()) != null)
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
            case NONE:
                if(instanceToSubscribersMap.remove(target) == null)
                    throwIllegalStateException("unregister", target, " not registered");
                break;
        }
    }

    @Override synchronized public void post(Object event) {
        busService.post(event);
    }

    void internalRegister() {
        //TODO
    }

    void internalUnregister() {
        //TODO
    }

    synchronized void internalRegisterSync() {
        internalRegister();
    }

    synchronized void internalUnregisterSync() {
        internalUnregister();
    }

    void registerSubscriber(Object target, ClassInfo classInfo) {
        List<EventSubscriber> subscribers;
        if(classInfo.isEmpty()) {
            subscribers = Collections.emptyList();
        } else {
            subscribers = busService.registerSubscribers(target, classInfo);
        }
        if (instanceToSubscribersMap.put(target, subscribers) != null)
            throwIllegalStateException("register", target, " already registered");
    }

    void registerProducer(Object target, ClassInfo classInfo) {
        List<EventProducer> producers;
        if(classInfo.isEmpty()) {
            producers = Collections.emptyList();
        } else {
            producers = busService.registerProducers(target, classInfo);
        }
        if (instanceToProducersMap.put(target, producers) != null)
            throwIllegalStateException("register", target, " already registered");
    }

    void unregisterSubscribers(Object target) {
        final List<EventSubscriber> subscribers = instanceToSubscribersMap.remove(target);
        if (subscribers == null) throwIllegalStateException("unregister", target, " not registered");
        else if(!subscribers.isEmpty()){
            busService.unregisterSubscribers(subscribers);
        }
    }

    void unregisterProducers(Object target) {
        final List<EventProducer> producers = instanceToProducersMap.remove(target);
        if (producers == null) throwIllegalStateException("unregister", target, " not registered");
        else if(!producers.isEmpty()) {
            busService.unregisterProducers(producers);
        }
    }

}
