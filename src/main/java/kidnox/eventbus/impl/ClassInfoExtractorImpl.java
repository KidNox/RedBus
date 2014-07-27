package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.utils.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

class ClassInfoExtractorImpl implements ClassInfoExtractor {

    final Map<Class, ClassType> classToTypeMap = new HashMap<Class, ClassType>();

    final Map<Class, ClassSubscribers> subscribersCache = new HashMap<Class, ClassSubscribers>();
    final Map<Class, ClassProducers> producersCache = new HashMap<Class, ClassProducers>();

    final Map<String, Dispatcher> dispatchersMap = new HashMap<String, Dispatcher>();

    final Dispatcher.Factory dispatcherFactory;

    ClassInfoExtractorImpl(Dispatcher.Factory factory) {
        this.dispatcherFactory = factory == null ? BusDefaults.createDefaultDispatcherFactory() : factory;
    }

    @Override public ClassType getTypeOf(Class clazz) {//TODO test it
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

    protected void saveSubscribers(Subscriber annotation, Class clazz) {//TODO
        Map<Class, Method> typedMethodsMap = null;
        final String value = annotation.value();

        for(Class mClass = clazz; checkSubscriberConditions(mClass, annotation, value, clazz);
            mClass = mClass.getSuperclass(), annotation = (Subscriber) mClass.getAnnotation(Subscriber.class)) {

            final Map<Class, Method> subscribers = getSubscribedMethods(mClass.getDeclaredMethods());
            if(subscribers.isEmpty())
                continue;

            if(typedMethodsMap == null)
                typedMethodsMap = new HashMap<Class, Method>();

            for(Map.Entry<Class, Method> entry : subscribers.entrySet()) {
                Method method = typedMethodsMap.put(entry.getKey(), entry.getValue());
                if(method != null) {
                    throw new IllegalStateException(String.format("To many subscribe methods in instance of %s, " +
                            "for event %s, can be only one", clazz.getName(), entry.getKey().getName()));
                }
            }
        }

        final Dispatcher dispatcher = getDispatcher(value);
        ClassSubscribers classSubscribers = typedMethodsMap == null ?
                ClassSubscribers.EMPTY : new ClassSubscribers(dispatcher, typedMethodsMap);
        subscribersCache.put(clazz, classSubscribers);
    }

    boolean checkSubscriberConditions(Class clazz, Subscriber annotation, String value, Class first) {
        if(clazz == null || annotation == null) {
            return false;
        } else if (!value.equals(annotation.value())){
            throw new IllegalArgumentException(String.format("dispatchers for child and parent classes does not match:"
                    +" child class = %s, dispatcher = %s, parent class = %s, dispatcher = %s",
                    first.getName(), value, clazz.getName(), annotation.value()));
        } else {
            return true;
        }
    }

    protected void saveProducers(Producer annotation, Class clazz) {
        Map<Class, Method> typedMethodsMap = null;
        for(Class mClass = clazz; clazz != null && annotation != null; mClass = mClass.getSuperclass(),
                annotation = (Producer) mClass.getAnnotation(Producer.class)) {

            final Map<Class, Method> producers = getProducerMethods(mClass.getDeclaredMethods());
            if(producers.isEmpty())
                continue;

            if(typedMethodsMap == null)
                typedMethodsMap = new HashMap<Class, Method>();

            for(Map.Entry<Class, Method> entry : producers.entrySet()) {
                Object value = typedMethodsMap.put(entry.getKey(), entry.getValue());
                if(value != null) {
                    throw new IllegalStateException(String.format("To many produce methods in instance of %s, " +
                            "for event %s, can be only one", clazz.getName(), entry.getKey().getName()));
                }
            }
        }

        ClassProducers classProducers = typedMethodsMap == null ?
                ClassProducers.EMPTY : new ClassProducers(typedMethodsMap);
        producersCache.put(clazz, classProducers);
    }

    protected Map<Class, Method> getSubscribedMethods(Method[] methods){
        Map<Class, Method> classToMethodMap = null;
        for(Method method : methods){
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

                final Class clazz = params[0];
//                if(clazz.isInterface())
//                    throw new IllegalArgumentException("Can't subscribe for interface.");
                classToMethodMap.put(clazz, method);
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
    }

    protected Map<Class, Method> getProducerMethods(Method[] methods) {
        Map<Class, Method> classToMethodMap = null;
        for(Method method : methods){
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

//                if(returnType.isInterface())
//                    throw new IllegalArgumentException("Can't produce interface.");
                classToMethodMap.put(returnType, method);
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
    }

    Dispatcher getDispatcher(String dispatcherName) {
        Dispatcher dispatcher = dispatchersMap.get(dispatcherName);
        if(dispatcher == null){
            dispatcher = dispatcherFactory.getDispatcher(dispatcherName);
            if(dispatcher == null){
                dispatcher = BusDefaults.DISPATCHER;
            }
            dispatchersMap.put(dispatcherName, dispatcher);
        }
        return dispatcher;
    }

}
