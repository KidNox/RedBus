package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ClassType;
import kidnox.eventbus.internal.ClassInfoExtractor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static kidnox.eventbus.internal.Utils.*;

public class ClassInfoExtractorImpl implements ClassInfoExtractor {

    final Map<Class, ClassInfo> classToInfoMap = newHashMap();

    @Override public ClassInfo getClassInfo(Class clazz) {
        ClassInfo info = classToInfoMap.get(clazz);
        if(info != null) return info;

        final Annotation[] annotations = clazz.getAnnotations();
        if(!isNullOrEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                if(annotation instanceof Subscriber) {
                    return extractSubscribers(clazz, (Subscriber) annotation);
                } else if (annotation instanceof Producer) {
                    return extractProducers(clazz, (Producer) annotation);
                }
            }
        }
        info = new ClassInfo(clazz);
        classToInfoMap.put(clazz, info);
        return info;
    }

    protected ClassInfo extractSubscribers(Class clazz, Subscriber annotation) {
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
        return new ClassInfo(clazz, ClassType.SUBSCRIBER, value, typedMethodsMap);
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

    protected ClassInfo extractProducers(Class clazz, Producer annotation) {
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
        return new ClassInfo(clazz, ClassType.PRODUCER, null, typedMethodsMap);
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
                    classToMethodMap = newHashMap(4);

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
                    classToMethodMap = newHashMap(4);

                if(classToMethodMap.put(returnType, method) != null)
                    throwMultiplyMethodsException(clazz, returnType, "produce");
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
    }

    protected void throwMultiplyMethodsException(Class clazz, Class event, String what) {
        throw new IllegalStateException(String.format("To many %s methods in instance of %s, " +
                "for event %s, can be only one.", what, clazz.getName(), event.getName()));
    }

}
