package kidnox.eventbus.impl;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.Element;
import kidnox.utils.Reflections;

import java.lang.reflect.Method;

public final class EventSubscriber extends Element {

    final Dispatcher dispatcher;
    final AsyncDispatcher asyncDispatcher;

    EventSubscriber(Class eventClass, Object target,
                    Method method, Dispatcher dispatcher) {
        super(eventClass, target, method);
        this.dispatcher = dispatcher;

        if (dispatcher instanceof AsyncDispatcher) {
            asyncDispatcher = (AsyncDispatcher) dispatcher;
        } else {
            asyncDispatcher = null;
        }
    }

    void dispatch(Object event) {
        if (asyncDispatcher != null && asyncDispatcher.inCurrentThread()) {
            invoke(event);
        } else {
            dispatcher.dispatchSubscribe(this, event);
        }
    }

    @Override protected Object invoke(Object event) {
        return Reflections.invokeMethod(target, method, event);
    }
}
