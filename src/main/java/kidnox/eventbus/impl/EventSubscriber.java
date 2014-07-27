package kidnox.eventbus.impl;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.Element;
import kidnox.eventbus.utils.Utils;

import java.lang.reflect.Method;

public final class EventSubscriber extends Element {

    final Dispatcher dispatcher;
    final AsyncDispatcher asyncDispatcher;

    protected EventSubscriber(Class eventClass, Object target, Method method, Dispatcher dispatcher) {
        super(eventClass, target, method);
        this.dispatcher = dispatcher;
        asyncDispatcher = dispatcher instanceof AsyncDispatcher ? (AsyncDispatcher) dispatcher : null;
    }

    void receive(Object event) {
        if (asyncDispatcher != null && asyncDispatcher.inCurrentThread()) {
            invoke(event);
        } else {
            dispatcher.dispatchSubscribe(this, event);
        }
    }

    @Override protected Object invoke(Object event) {
        return Utils.invokeMethod(target, method, event);
    }
}
