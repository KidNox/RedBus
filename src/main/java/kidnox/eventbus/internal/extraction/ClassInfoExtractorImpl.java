package kidnox.eventbus.internal.extraction;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.BusException;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ClassType;
import kidnox.eventbus.internal.element.ElementInfo;
import kidnox.eventbus.internal.element.ElementType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static kidnox.eventbus.internal.Utils.*;

public class ClassInfoExtractorImpl implements ClassInfoExtractor {

    private final Map<Class<? extends Annotation>, ClassExtractionStrategy> extractionStrategyMap = newHashMap(4);

    {
        extractionStrategyMap.put(Subscriber.class, new SubscriberExtractor());
        extractionStrategyMap.put(Producer.class, new ProducerExtractor());
        extractionStrategyMap.put(EventServiceFactory.class, new ServiceExtractor());
        extractionStrategyMap.put(EventTask.class, new TaskExtractor());
    }

    final Map<Class, ClassInfo> classInfoCache = newHashMap();

    @SuppressWarnings("unchecked")
    @Override public ClassInfo getClassInfo(Class clazz) {
        ClassInfo info = classInfoCache.get(clazz);
        if(info != null) return info;

        final Annotation[] annotations = clazz.getAnnotations();
        if(!isNullOrEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                ClassExtractionStrategy extractionStrategy = extractionStrategyMap.get(annotation.annotationType());
                if(extractionStrategy != null) {
                    info = extractionStrategy.extract(clazz, annotation);
                    break;
                }
            }
        }
        if(info == null) info = new ClassInfo(clazz);//info for none type
        classInfoCache.put(clazz, info);
        return info;
    }

    protected ClassInfo extractSubscribers(Class clazz, Subscriber annotation) {
        Map<Class, ElementInfo> elementsInfoMap = null;
        final String value = annotation.value();

        for(Class mClass = clazz; checkSubscriber(mClass, clazz, value); mClass = mClass.getSuperclass()) {
            final Set<ElementInfo> subscribers = getSubscribedMethods(mClass);
            if(subscribers == null)
                continue;

            if(elementsInfoMap == null) elementsInfoMap = newHashMap(8);

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

    protected ClassInfo extractProducers(Class clazz, Producer annotation) {
        Map<Class, ElementInfo> elementsInfoMap = null;
        final String value = annotation.value();

        for(Class mClass = clazz; checkProducer(mClass, clazz, value); mClass = mClass.getSuperclass()) {
            final Set<ElementInfo> producers = getProducerMethods(mClass);
            if(producers == null)
                continue;

            if(elementsInfoMap == null) elementsInfoMap = newHashMap(8);

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
        return new ClassInfo(clazz, ClassType.PRODUCER, value, values);
    }

    protected boolean checkSubscriber(Class current, Class child, String childAnnotationValue) {
        return current.getAnnotation(Subscriber.class) != null;
    }

    protected boolean checkProducer(Class current, Class child, String childAnnotationValue) {
        return current.getAnnotation(Producer.class) != null;
    }

    protected ClassInfo extractService(Class clazz, EventServiceFactory annotation) {
        return new ClassInfo(clazz, ClassType.SERVICE, annotation.value(), getServiceMethods(clazz));
    }

    protected ClassInfo extractTask(Class clazz, EventTask annotation) {

        return null;//TODO
    }

    protected Set<ElementInfo> getSubscribedMethods(Class clazz) {//TODO need map
        Set<ElementInfo> elementInfoSet = null;
        for(Method method : clazz.getDeclaredMethods()){
            if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                continue;

            final Class[] params = method.getParameterTypes();
            if(params.length != 1)
                continue;
            Class paramType = params[0];
            Class returnType = method.getReturnType();

            ElementType elementType = null;
            if(returnType == void.class && method.isAnnotationPresent(Subscribe.class)) {
                elementType = ElementType.SUBSCRIBE;
            } else if(returnType != void.class && method.isAnnotationPresent(Handle.class)) {
                if(returnType == paramType)
                    throw new BusException("Process method must return different type than param type");
                elementType = ElementType.PROCESS;
            }

            if(elementType != null) {
                if(elementInfoSet == null) elementInfoSet = newHashSet(8);

                if(!elementInfoSet.add(new ElementInfo(elementType, paramType, method)))
                    throwMultiplyMethodsException(clazz, paramType, elementType.name().toLowerCase());
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

            if(method.isAnnotationPresent(Produce.class)) {
                if(elementInfoSet == null) elementInfoSet = newHashSet(4);

                if(!elementInfoSet.add(new ElementInfo(ElementType.PRODUCE, returnType, method)))
                    throwMultiplyMethodsException(clazz, returnType, "produce");
            }
        }
        return elementInfoSet;
    }

    protected Set<ElementInfo> getServiceMethods(Class clazz) {
        Set<ElementInfo> elementInfoSet = null;
        for(Method method : clazz.getDeclaredMethods()) {
            if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                continue;

            final Class returnType = method.getReturnType();
            if(returnType == void.class)
                continue;
            if(method.getParameterTypes().length != 0)
                continue;

            if(method.isAnnotationPresent(EventService.class)) {
                if(elementInfoSet == null) elementInfoSet = newHashSet(4);

                elementInfoSet.add(new ElementInfo(ElementType.SERVICE, returnType, method));
            }
        }
        return elementInfoSet;
    }

    protected void throwMultiplyMethodsException(Class clazz, Class event, String what) {
        throw new BusException(String.format("To many %s methods in instance of %s, " +
                "for event %s, can be only one.", what, clazz.getName(), event.getName()));
    }


    class SubscriberExtractor implements ClassExtractionStrategy<Subscriber> {
        @Override public ClassInfo extract(Class clazz, Subscriber annotation) {
            return extractSubscribers(clazz, annotation);
        }
    }

    class ProducerExtractor implements ClassExtractionStrategy<Producer> {
        @Override public ClassInfo extract(Class clazz, Producer annotation) {
            return extractProducers(clazz, annotation);
        }
    }

    class ServiceExtractor implements ClassExtractionStrategy<EventServiceFactory> {
        @Override public ClassInfo extract(Class clazz, EventServiceFactory annotation) {
            return extractService(clazz, annotation);
        }
    }

    class TaskExtractor implements ClassExtractionStrategy<EventTask> {
        @Override public ClassInfo extract(Class clazz, EventTask annotation) {
            return extractTask(clazz, annotation);
        }
    }

}
