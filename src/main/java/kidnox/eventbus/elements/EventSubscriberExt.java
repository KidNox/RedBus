package kidnox.eventbus.elements;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.ExceptionHandler;

import java.lang.reflect.Method;

public class EventSubscriberExt extends EventSubscriber {

    public EventSubscriberExt(Class eventClass, Object target, Method method,
                              EventDispatcher dispatcher, ExceptionHandler exceptionHandler) {
        super(eventClass, target, method, dispatcher, exceptionHandler);
    }
}
