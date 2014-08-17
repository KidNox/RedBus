package kidnox.eventbus.impl;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

import static kidnox.eventbus.internal.Utils.newHashMap;

public class ClassInfoExtractorValidation extends ClassInfoExtractorImpl {

    @Override protected Map<Class, Method> getSubscribedMethods(Class clazz) {
        Map<Class, Method> classToMethodMap = null;
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

                if (classToMethodMap == null)
                    classToMethodMap = newHashMap(4);

                final Class eventClass = params[0];
                if (eventClass.isInterface())
                    throw new IllegalArgumentException("Method " + method + " can't subscribe for interface.");

                if(classToMethodMap.put(params[0], method) != null)
                    throwMultiplyMethodsException(clazz, params[0], "subscribe");
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
    }

    @Override protected Map<Class, Method> getProducerMethods(Class clazz) {
        Map<Class, Method> classToMethodMap = null;
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

                if (classToMethodMap == null)
                    classToMethodMap = newHashMap(4);

                if (returnType.isInterface())
                    throw new IllegalArgumentException("Method " + method + " can't produce interface.");

                if(classToMethodMap.put(returnType, method) != null)
                    throwMultiplyMethodsException(clazz, returnType, "produce");
            }
        }
        return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
    }
}
