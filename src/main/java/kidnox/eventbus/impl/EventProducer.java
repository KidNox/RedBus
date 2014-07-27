package kidnox.eventbus.impl;

import kidnox.eventbus.Element;
import kidnox.eventbus.utils.Utils;

import java.lang.reflect.Method;

public final class EventProducer extends Element {

    EventProducer(Class eventClass, Object target, Method method) {
        super(eventClass, target, method);
    }

    @Override protected Object invoke(Object event) {
        return Utils.invokeMethod(target, method);
    }
}
