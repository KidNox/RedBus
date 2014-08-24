package kidnox.eventbus.internal;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Subscribe;
import kidnox.eventbus.internal.element.ElementInfo;
import kidnox.eventbus.internal.element.ElementType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static kidnox.eventbus.internal.Utils.newHashSet;

public class ClassInfoExtractorValidation extends ClassInfoExtractorImpl {

    @Override protected Set<ElementInfo> getSubscribedMethods(Class clazz) {
        Set<ElementInfo> elementInfoSet = null;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {

                if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                    throw new IllegalArgumentException("Method " + method + " with @Subscribe must be public.");

                if (method.getReturnType() != void.class)
                    throw new IllegalArgumentException("Method " + method
                            + " with @Subscribe must return void type.");

                final Class[] params = method.getParameterTypes();
                if (params.length != 1)
                    throw new IllegalArgumentException("Methods " + method
                            + " with @Subscribe must require a single argument.");

                if (elementInfoSet == null)
                    elementInfoSet = newHashSet(8);

                final Class type = params[0];
                if (type.isInterface())
                    throw new IllegalArgumentException("Method " + method + " can't subscribe for interface.");

                if(!elementInfoSet.add(new ElementInfo(ElementType.SUBSCRIBE, type, method)))
                    throwMultiplyMethodsException(clazz, type, "subscribe");
            }
        }
        return elementInfoSet;
    }

    @Override protected Set<ElementInfo> getProducerMethods(Class clazz) {
        Set<ElementInfo> elementInfoSet = null;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Produce.class)) {

                if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                    throw new IllegalArgumentException("Method " + method + " with @Produce must be public.");

                final Class returnType = method.getReturnType();
                if (returnType == void.class)
                    throw new IllegalArgumentException("Method " + method + " with @Produce can't return void type.");
                if (method.getParameterTypes().length != 0)
                    throw new IllegalArgumentException("Methods " + method
                            + " with @Produce must require zero arguments.");

                if (elementInfoSet == null)
                    elementInfoSet = newHashSet(4);

                if (returnType.isInterface())
                    throw new IllegalArgumentException("Method " + method + " can't produce interface.");

                if(!elementInfoSet.add(new ElementInfo(ElementType.PRODUCE, returnType, method)))
                    throwMultiplyMethodsException(clazz, returnType, "produce");
            }
        }
        return elementInfoSet;
    }

    @Override protected Set<ElementInfo> getServiceMethods(Class clazz) {
        return super.getServiceMethods(clazz);//TODO
    }

//    @Override protected boolean checkSubscriber(Class current, Class child, String childAnnotationValue) {
//        Subscriber subscriber = (Subscriber) current.getAnnotation(Subscriber.class);
//        if(subscriber == null) return false;
//        checkAnnotationValue(current, subscriber.value(), childAnnotationValue, child);
//        return true;
//    }
//
//    @Override protected boolean checkProducer(Class current, Class child, String childAnnotationValue) {
//        Producer producer = (Producer) current.getAnnotation(Producer.class);
//        if(producer == null) return false;
//        checkAnnotationValue(current, producer.value(), childAnnotationValue, child);
//        return true;
//    }
//
//    protected void checkAnnotationValue(Class clazz, String annotationValue, String baseValue, Class child) {
//        if (!baseValue.equals(annotationValue)){
//            throw new BusException(String.format("dispatchers for child and parent classes does not match:"
//                            + " child class = %s, dispatcher = %s, parent class = %s, dispatcher = %s.",
//                    child.getName(), baseValue, clazz.getName(), annotationValue));
//        }
//    }

}
