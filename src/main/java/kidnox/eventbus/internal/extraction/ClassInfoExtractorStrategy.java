package kidnox.eventbus.internal.extraction;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.*;
import kidnox.eventbus.internal.element.ElementInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

import static kidnox.eventbus.internal.Utils.*;
import static kidnox.eventbus.internal.extraction.ExtractionUtils.*;

@SuppressWarnings("unchecked")
interface ClassInfoExtractorStrategy<T extends Annotation> {

    ClassInfo extract(Class clazz, T annotation);


    ClassInfoExtractorStrategy<Subscriber> SUBSCRIBER = new InheritanceSupportedStrategy<Subscriber>
            (ClassType.SUBSCRIBER, OnRegister.class, OnUnregister.class, Subscribe.class, Handle.class) {

        @Override String getAnnotationValue(Subscriber annotation) {
            return annotation.value();
        }

        @Override boolean check(Class current, Class child, String childAnnotationValue) {
            Subscriber subscriber = (Subscriber) current.getAnnotation(Subscriber.class);
            if(subscriber == null) return false;
            checkAnnotationValue(current, subscriber.value(), childAnnotationValue, child);
            return true;
        }

        @Override boolean resolveSameKeysElements(Class clazz, ElementInfo newElement, ElementInfo oldElement) {
            if(super.resolveSameKeysElements(clazz, newElement, oldElement)) return true;
            if(newElement.elementType != oldElement.elementType) {
                throw new BusException("%s can contain only one subscribe or handle method for %s event",
                        clazz.getName(), newElement.eventType.getName());
            }
            throw new BusException("To many %s methods in instance of %s, for event %s, can be only one.",
                    newElement.elementType.toString().toLowerCase(), clazz.getName(), newElement.eventType);
        }
    };

    ClassInfoExtractorStrategy<Producer> PRODUCER = new InheritanceSupportedStrategy<Producer>
            (ClassType.PRODUCER, OnRegister.class, OnUnregister.class, Produce.class) {

        @Override String getAnnotationValue(Producer annotation) {
            return annotation.value();
        }

        @Override boolean check(Class current, Class child, String childAnnotationValue) {
            Producer producer = (Producer) current.getAnnotation(Producer.class);
            if (producer == null) return false;
            checkAnnotationValue(current, producer.value(), childAnnotationValue, child);
            return true;
        }

        @Override boolean resolveSameKeysElements(Class clazz, ElementInfo newElement, ElementInfo oldElement) {
            if(super.resolveSameKeysElements(clazz, newElement, oldElement)) return true;
            throw new BusException("To many produce methods in instance of %s, for event %s, can be only one.",
                    clazz.getName(), newElement.eventType);
        }
    };

    ClassInfoExtractorStrategy<Task> TASK = new InheritanceNotSupportedStrategy<Task>
            (ClassType.TASK, OnRegister.class, OnUnregister.class, Execute.class) {

        @Override String getAnnotationValue(Task annotation) {
            return annotation.value();
        }

        @Override boolean resolveSameKeysElements(Class clazz, ElementInfo newElement, ElementInfo oldElement) {
            if(super.resolveSameKeysElements(clazz, newElement, oldElement)) return true;
            throw new BusException("Task %s can contain only one @Execute method.", clazz.getName());
        }
    };

    abstract class InheritanceSupportedStrategy<T extends Annotation> extends AbstractStrategy<T> {

        InheritanceSupportedStrategy(ClassType type, Class<? extends Annotation>... args) {
            super(type, args);
        }

        @Override public ClassInfo extract(Class clazz, T annotation) {
            Map<Class, ElementInfo> elementsInfoMap = null;
            String value = getAnnotationValue(annotation);
            for(Class mClass = clazz; check(mClass, clazz, value); mClass = mClass.getSuperclass()) {
                elementsInfoMap = fillElementsInfoMap(elementsInfoMap, mClass.getDeclaredMethods(), clazz);
            }
            return createInfo(clazz, type, value, elementsInfoMap);
        }

        @Override boolean resolveSameKeysElements(Class clazz, ElementInfo newElement, ElementInfo oldElement) {
            //noinspection SimplifiableIfStatement
            if(super.resolveSameKeysElements(clazz, newElement, oldElement)) return true;
            return oldElement.method.getName().equals(newElement.method.getName());
        }

        abstract String getAnnotationValue(T annotation);

        abstract boolean check(Class current, Class child, String childAnnotationValue);
    }

    abstract class InheritanceNotSupportedStrategy<T extends Annotation> extends AbstractStrategy<T> {
        InheritanceNotSupportedStrategy(ClassType type, Class<? extends Annotation>... args) {
            super(type, args);
        }

        @Override public ClassInfo extract(Class clazz, T annotation) {
            Map<Class, ElementInfo> elementsInfoMap = fillElementsInfoMap(null, clazz.getDeclaredMethods(), clazz);
            String value = getAnnotationValue(annotation);
            return createInfo(clazz, type, value, elementsInfoMap);
        }

        abstract String getAnnotationValue(T annotation);
    }

    abstract class AbstractStrategy<T extends Annotation> implements ClassInfoExtractorStrategy<T> {

        final ClassType type;
        final Map<Class<? extends Annotation>, ElementExtractionStrategy> strategyMap;

        //allowed annotations for getElementStrategy
        AbstractStrategy(ClassType type, Class<? extends Annotation>... args) {
            this.type = type;
            this.strategyMap = getElementStrategiesFor(args);
        }

        Map<Class, ElementInfo> fillElementsInfoMap(Map<Class, ElementInfo> elements, Method[] methods, Class clazz) {
            for(Method method : methods) {
                if ((method.getModifiers() & Modifier.PUBLIC) == 0) continue; //ignore all not public methods

                Annotation[] annotations = method.getDeclaredAnnotations();
                if (isNullOrEmpty(annotations)) continue;
                if(method.isBridge()) continue; //java 8 annotated bridge methods fix

                for (Annotation mAnnotation : annotations) {
                    ElementExtractionStrategy strategy = getElementStrategy(mAnnotation, type, clazz);
                    if (strategy == null) continue;
                    if (elements == null) elements = newHashMap(8);

                    ElementInfo element = strategy.extract(method, clazz);
                    ElementInfo oldEntry = elements.put(element.eventType, element);
                    if(oldEntry != null) resolveSameKeysElements(clazz, element, oldEntry);
                    break; //we find strategy for annotation and extract element so can break
                }
            }
            return elements;
        }

        ElementExtractionStrategy getElementStrategy(Annotation elementAnnotation, ClassType classType, Class clazz) {
            Class<? extends Annotation> elementType = elementAnnotation.annotationType();
            ElementExtractionStrategy strategy = strategyMap.get(elementType);
            if (strategy == null && ELEMENT_STRATEGIES.containsKey(elementType))
                throw new BusException("Annotation %s not allowed in class %s that annotated as %s",
                        elementType.getSimpleName(), clazz.getName(), classType);
            return strategy;
        }

        ClassInfo createInfo(Class clazz, ClassType type, String annotationValue, Map<Class, ElementInfo> elementsMap) {
            elementsMap = elementsMap == null ? Collections.<Class, ElementInfo>emptyMap() : elementsMap;
            return new ClassInfo(clazz, type, annotationValue, elementsMap);
        }

        boolean resolveSameKeysElements(Class clazz, ElementInfo newElement, ElementInfo oldElement) {
            if(oldElement.eventType == Utils.REGISTER_KEY)
                throw new BusException("Class %s can contain only one @OnRegister method", clazz.getName());
            if(oldElement.eventType == Utils.UNREGISTER_VOID_KEY)
                throw new BusException("Class %s can contain only one @OnUnregister method");
            return false;
        }
    }
}
