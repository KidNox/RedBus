package kidnox.eventbus.elements;

import java.lang.reflect.Method;

public final class EventProducer extends Element {

    public EventProducer(Class eventClass, Object target, Method method) {
        super(eventClass, target, method);
    }

}
