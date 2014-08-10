package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.elements.ClassProducers;
import kidnox.eventbus.elements.ClassSubscribers;
import kidnox.eventbus.internal.ClassType;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.utils.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ClassInfoExtractorImpl implements ClassInfoExtractor {

    final Map<Class, ClassType> classToTypeMap = new HashMap<Class, ClassType>();

    final Map<Class, ClassSubscribers> subscribersCache = new HashMap<Class, ClassSubscribers>();
    final Map<Class, ClassProducers> producersCache = new HashMap<Class, ClassProducers>();

    final Map<String, EventDispatcher> dispatchersMap = new HashMap<String, EventDispatcher>();

    final EventDispatcher.Factory dispatcherFactory;

    public ClassInfoExtractorImpl(EventDispatcher.Factory factory) {
        this.dispatcherFactory = factory == null ? InternalFactory.createDefaultEventDispatcherFactory() : factory;
    }

    @Override public ClassType getTypeOf(Class clazz) {
        ClassType type = classToTypeMap.get(clazz);
        if(type != null) return type;

        type = ClassType.NONE;
        final Annotation[] annotations = clazz.getAnnotations();
        if(!Utils.isNullOrEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                if(annotation instanceof Subscriber) {
                    type = ClassType.SUBSCRIBER;
                    saveSubscribers((Subscriber) annotation, clazz);
                    break;
                } else if (annotation instanceof Producer) {
                    type = ClassType.PRODUCER;
                    saveProducers((Producer) annotation, clazz);
                    break;
                }
            }
        }
        classToTypeMap.put(clazz, type);
        return type;
    }

    @Override public ClassSubscribers getClassSubscribers(Class clazz) {
        return subscribersCache.get(clazz);
    }

    @Override public ClassProducers getClassProducers(Class clazz) {
        return producersCache.get(clazz);
    }

    protected void saveSubscribers(Subscriber annotation, Class clazz) {
        Map<Class, Method> typedMethodsMap = null;
        final String value = annotation.value();

        for(Class mClass = clazz; checkSubscriberConditions(mClass, annotation, value, clazz);
            mClass = mClass.getSuperclass(), annotation = (Subscriber) mClass.getAnnotation(Subscriber.class)) {

            final Map<Class, Method> subscribers = getSubscribedMethods(mClass);
            if(subscribers.isEmpty())
                continue;

            if(typedMethodsMap == null)
                typedMethodsMap = new HashMap<Class, Method>();

            for(Map.Entry<Class, Method> entry : subscribers.entrySet()) {
                Method method = typedMethodsMap.put(entry.getKey(), entry.getValue());
                if(method != null) {
                    //overridden method check
                    if(method.getName().equals(entry.getValue().getName())) {
                        typedMethodsMap.put(entry.getKey(), method);
                    } else {
                        throwMultiplyMethodsException(clazz, entry.getKey(), "subscribe");
                    }
                }
            }
        }
        final EventDispatcher dispatcher = getDispatcher(value);
        ClassSubscribers classSubscribers = typedMethodsMap == null ?
                ClassSubscribers.EMPTY : new ClassSubscribers(dispatcher, typedMethodsMap);
        subscribersCache.put(clazz, classSubscribers);
    }

    protected boolean checkSubscriberConditions(Class clazz, Subscriber annotation, String value, Class first) {
        if(clazz == null || annotation == null) {
            return false;
        } else if (!value.equals(annotation.value())){
            throw new IllegalArgumentException(String.format("dispatchers for child and parent classes does not match:"
                    +" child class = %s, dispatcher = %s, parent class = %s, dispatcher = %s.",
                    first.getName(), value, clazz.getName(), annotation.value()));
        } else {
            return true;
        }
    }

    protected void saveProducers(Producer annotation, Class clazz) {
        Map<Class, Method> typedMethodsMap = null;
        for(Class mClass = clazz; clazz != null && annotation != null; mClass = mClass.getSuperclass(),
                annotation = (Producer) mClass.getAnnotation(Producer.class)) {

            final Map<Class, Method> producers = getProducerMethods(mClass);
            if(producers.isEmpty())
                continue;

            if(typedMethodsMap == null)
                typedMethodsMap = new HashMap<Class, Method>();

            for(Map.Entry<Class, Method> entry : producers.entrySet()) {
                Method method = typedMethodsMap.put(entry.getKey(), entry.getValue());
                if(method != null) {
                    //overridden method check
                    if(method.getName().equals(entry.getValue().getName())) {
                        typedMethodsMap.put(entry.getKey(), method);
                    } else {
                        throwMultiplyMethodsException(clazz, entry.getKey(), "produce");
                    }
                }
            }
        }
        ClassProducers classProducers = typedMethodsMap == null ?
                ClassProducers.EMPTY : new ClassProducers(typedMethodsMap);
        producersCache.put(clazz, classProducers);
    }

    protected Map<Class, Method> getSubscribedMethods(Class clazz){
        Map<Class, Method> classToMethodMap = null;
        for(Method method : clazz.getDeclaredMethods()){
            if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                continue;
            if(method.getReturnType() != void.class)
                continue;

            final Class[] params = method.getParameterTypes();
            if(params.length != 1)
                continue;

            if(method.isAnnotationPresent(Subscribe.class)){
                if(classToMethodMap == null)
                    classToMethodMap = new HashMap<Class, Method>();

                if(classToMethodMap.put(params[0], method) != null)
                    throwMultiplyMethodsException(clazz, params[0], "subscribe");
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
    }

    protected Map<Class, Method> getProducerMethods(Class clazz) {
        Map<Class, Method> classToMethodMap = null;
        for(Method method : clazz.getDeclaredMethods()){
            if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                continue;

            final Class returnType = method.getReturnType();
            if(returnType == void.class)
                continue;
            if(method.getParameterTypes().length != 0)
                continue;

            if(method.isAnnotationPresent(Produce.class)){
                if(classToMethodMap == null)
                    classToMethodMap = new HashMap<Class, Method>();

                if(classToMethodMap.put(returnType, method) != null)
                    throwMultiplyMethodsException(clazz, returnType, "produce");
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
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

    protected void throwMultiplyMethodsException(Class clazz, Class event, String what) {
        throw new IllegalStateException(String.format("To many %s methods in instance of %s, " +
                "for event %s, can be only one.", what, clazz.getName(), event.getName()));
    }

}
