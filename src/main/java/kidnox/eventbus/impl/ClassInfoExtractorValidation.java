package kidnox.eventbus.impl;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.Produce;
import kidnox.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class ClassInfoExtractorValidation extends ClassInfoExtractorImpl {

    ClassInfoExtractorValidation(Dispatcher.Factory factory) {
        super(factory);
    }

    @Override protected Map<Class, Method> getSubscribedMethods(Method[] methods) {
        Map<Class, Method> classToMethodMap = null;
        for (Method method : methods) {
            if (method.isAnnotationPresent(Subscribe.class)) {

                if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                    throw new IllegalArgumentException("Method " + method + " with @Subscribe must be public.");

                if (method.getReturnType() != void.class)
                    throw new IllegalArgumentException("Method with" + method
                            + " with @Subscribe must return void type.");

                final Class[] params = method.getParameterTypes();
                if (params.length != 1)
                    throw new IllegalArgumentException("Methods " + method
                            + " with @Subscribe must require a single argument.");

                if (classToMethodMap == null)
                    classToMethodMap = new HashMap<Class, Method>();

                final Class clazz = params[0];
                if (clazz.isInterface())
                    throw new IllegalArgumentException("Method " + method + " can't subscribe for interface.");
                classToMethodMap.put(clazz, method);
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
    }

    @Override protected Map<Class, Method> getProducerMethods(Method[] methods) {
        Map<Class, Method> classToMethodMap = null;
        for (Method method : methods) {
            if (method.isAnnotationPresent(Produce.class)) {

                if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                    throw new IllegalArgumentException("Method " + method + " with @Produce must be public.");

                final Class returnType = method.getReturnType();
                if (returnType == void.class)
                    throw new IllegalArgumentException("Method " + method + " with @Produce can't return void type.");
                if (method.getParameterTypes().length != 0)
                    throw new IllegalArgumentException("Methods " + method
                            + " with @Produce must require zero arguments.");

                if (classToMethodMap == null)
                    classToMethodMap = new HashMap<Class, Method>();

                if (returnType.isInterface())
                    throw new IllegalArgumentException("Method " + method + " can't produce interface.");
                classToMethodMap.put(returnType, method);
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
    }
}
