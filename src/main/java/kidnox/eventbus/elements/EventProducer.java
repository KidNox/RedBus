package kidnox.eventbus.elements;

import kidnox.eventbus.utils.Utils;

import java.lang.reflect.Method;

public final class EventProducer extends Element {

    public EventProducer(Class eventClass, Object target, Method method) {
        super(eventClass, target, method);
    }

    @Override public Object invoke(Object event) {
        return Utils.invokeMethod(target, method);
    }

}
