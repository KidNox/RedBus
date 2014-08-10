package kidnox.eventbus.elements;

import kidnox.eventbus.ExceptionHandler;

import java.lang.reflect.Method;

public final class EventProducer extends Element {

    public EventProducer(Class eventClass, Object target, Method method, ExceptionHandler exceptionHandler) {
        super(eventClass, target, method, exceptionHandler);
    }

    @Override public Object invoke(Object event) {
        return invokeMethod();
    }

}
