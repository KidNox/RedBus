package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.*;

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
                    info =  extractSubscribers(clazz, (Subscriber) annotation);
                    break;
                } else if (annotation instanceof Producer) {
                    info = extractProducers(clazz, (Producer) annotation);
                    break;
                }
            }
        }
        if(info == null) info = new ClassInfo(clazz);
        classToInfoMap.put(clazz, info);
        return info;
    }

    protected ClassInfo extractSubscribers(Class clazz, Subscriber annotation) {
        Map<Class, ElementInfo> elementsInfoMap = null;
        final String value = annotation.value();

        for(Class mClass = clazz; checkSubscriberConditions(mClass, annotation, value, clazz);
            mClass = mClass.getSuperclass(), annotation = (Subscriber) mClass.getAnnotation(Subscriber.class)) {

            final Set<ElementInfo> subscribers = getSubscribedMethods(mClass);
            if(subscribers == null)
                continue;

            if(elementsInfoMap == null){
                elementsInfoMap = newHashMap(8);
            }

            for(ElementInfo entry : subscribers) {
                ElementInfo oldEntry = elementsInfoMap.put(entry.eventType, entry);
                if(oldEntry != null) {
                    //overridden method check
                    if(oldEntry.method.getName().equals(entry.method.getName())) {
                        elementsInfoMap.put(entry.eventType, entry);
                    } else {
                        throwMultiplyMethodsException(clazz, entry.eventType, "subscribe");
                    }
                }
            }
        }
        Collection<ElementInfo> values = elementsInfoMap == null ? null : elementsInfoMap.values();
        return new ClassInfo(clazz, ClassType.SUBSCRIBER, value, values);
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
        Map<Class, ElementInfo> elementsInfoMap = null;
        for(Class mClass = clazz; clazz != null && annotation != null; mClass = mClass.getSuperclass(),
                annotation = (Producer) mClass.getAnnotation(Producer.class)) {

            final Set<ElementInfo> producers = getProducerMethods(mClass);
            if(producers == null)
                continue;

            if(elementsInfoMap == null)
                elementsInfoMap = newHashMap(8);

            for(ElementInfo entry : producers) {
                ElementInfo oldEntry = elementsInfoMap.put(entry.eventType, entry);
                if(oldEntry != null) {
                    //overridden method check
                    if(oldEntry.method.getName().equals(entry.method.getName())) {
                        elementsInfoMap.put(entry.eventType, entry);
                    } else {
                        throwMultiplyMethodsException(clazz, entry.eventType, "produce");
                    }
                }
            }
        }
        Collection<ElementInfo> values = elementsInfoMap == null ? null : elementsInfoMap.values();
        return new ClassInfo(clazz, ClassType.PRODUCER, null, values);
    }

    protected Set<ElementInfo> getSubscribedMethods(Class clazz){
        Set<ElementInfo> elementInfoSet = null;
        for(Method method : clazz.getDeclaredMethods()){
            if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                continue;

            final Class[] params = method.getParameterTypes();
            if(params.length != 1)
                continue;
            //TODO add Process annotation
            if(method.getReturnType() != void.class)
                continue;

            if(method.isAnnotationPresent(Subscribe.class)){
                if(elementInfoSet == null)
                    elementInfoSet = newHashSet(8);

                Class type = params[0];
                if(!elementInfoSet.add(new ElementInfo(ElementType.SUBSCRIBE, type, method)))
                    throwMultiplyMethodsException(clazz, type, "subscribe");
            }
        }
        return elementInfoSet;
    }

    protected Set<ElementInfo> getProducerMethods(Class clazz) {
        Set<ElementInfo> elementInfoSet = null;
        for(Method method : clazz.getDeclaredMethods()){
            if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                continue;

            final Class returnType = method.getReturnType();
            if(returnType == void.class)
                continue;
            if(method.getParameterTypes().length != 0)
                continue;

            if(method.isAnnotationPresent(Produce.class)){
                if(elementInfoSet == null)
                    elementInfoSet = newHashSet(4);

                if(!elementInfoSet.add(new ElementInfo(ElementType.PRODUCE, returnType, method)))
                    throwMultiplyMethodsException(clazz, returnType, "produce");
            }
        }
        return elementInfoSet;
    }

    protected void throwMultiplyMethodsException(Class clazz, Class event, String what) {
        throw new IllegalStateException(String.format("To many %s methods in instance of %s, " +
                "for event %s, can be only one.", what, clazz.getName(), event.getName()));
    }

}
