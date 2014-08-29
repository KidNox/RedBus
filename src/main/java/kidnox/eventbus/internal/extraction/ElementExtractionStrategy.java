package kidnox.eventbus.internal.extraction;

import kidnox.eventbus.internal.element.ElementInfo;
import kidnox.eventbus.internal.element.ElementType;

import java.lang.reflect.Method;

import static kidnox.eventbus.internal.extraction.ExtractionUtils.throwBadMethodException;

interface ElementExtractionStrategy {//TODO

    ElementInfo extract(Method method, Class target);

    ElementExtractionStrategy REGISTER = new ElementExtractionStrategy() {
        @Override public ElementInfo extract(Method method, Class target) {
            return null;
        }
    };

    ElementExtractionStrategy UNREGISTER = new ElementExtractionStrategy() {
        @Override public ElementInfo extract(Method method, Class target) {
            return null;
        }
    };

    ElementExtractionStrategy SUBSCRIBE = new ElementExtractionStrategy() {
        @Override public ElementInfo extract(Method method, Class target) {
            if (method.getReturnType() != void.class)
                throwBadMethodException(method, "with @Subscribe must return void type. Try @Handle for this case");

            final Class[] params = method.getParameterTypes();
            if (params.length != 1)
                throwBadMethodException(method, "with @Subscribe must require a single argument.");

            final Class type = params[0];
            if (type.isInterface())
                throwBadMethodException(method, "can't subscribe for interface.");

            return new ElementInfo(ElementType.SUBSCRIBE, type, method);
        }
    };

    ElementExtractionStrategy PRODUCE = new ElementExtractionStrategy() {
        @Override public ElementInfo extract(Method method, Class target) {
            final Class returnType = method.getReturnType();
            if (returnType == void.class)
                throwBadMethodException(method, "with @Produce can't return void type.");
            if (method.getParameterTypes().length != 0)
                throwBadMethodException(method, "with @Produce must require zero arguments.");

            if (returnType.isInterface())
                throwBadMethodException(method, "can't produce interface.");

            return new ElementInfo(ElementType.PRODUCE, returnType, method);
        }
    };

    ElementExtractionStrategy HANDLE = new ElementExtractionStrategy() {
        @Override public ElementInfo extract(Method method, Class target) {
            if (method.getReturnType() == void.class)
                throwBadMethodException(method, "with @Handle must return not void type. Try @Subscribe for this case");

            final Class[] params = method.getParameterTypes();
            if (params.length != 1)
                throwBadMethodException(method, "with @Handle must require a single argument.");

            final Class type = params[0];
            if (type.isInterface())
                throwBadMethodException(method, "can't handle interface.");

            return new ElementInfo(ElementType.HANDLE, type, method);
        }
    };

    ElementExtractionStrategy EXECUTE = new ElementExtractionStrategy() {
        @Override public ElementInfo extract(Method method, Class target) {
            return null;
        }
    };

    ElementExtractionStrategy SERVICE = new ElementExtractionStrategy() {
        @Override public ElementInfo extract(Method method, Class target) {
            return new ElementInfo(ElementType.SERVICE, method.getReturnType(), method);//TODO
        }
    };

}