package kidnox.eventbus.impl;


import kidnox.annotations.Internal;
import kidnox.annotations.NotNull;
import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.Element;
import kidnox.utils.Reflections;

import java.lang.reflect.Method;

@Internal
public class EventSubscriber extends Element {

    protected final Dispatcher dispatcher;
    protected final AsyncDispatcher asyncDispatcher;

    protected EventSubscriber(@NotNull Class eventClass, @NotNull Object target,
                              @NotNull Method method, @NotNull Dispatcher dispatcher) {
        super(eventClass, target, method);
        this.dispatcher = dispatcher;

        if (dispatcher instanceof AsyncDispatcher) {
            asyncDispatcher = (AsyncDispatcher) dispatcher;
        } else {
            asyncDispatcher = null;
        }
    }

    protected void dispatch(Object event) {
        if (asyncDispatcher != null && asyncDispatcher.isCurrentThread()) {
            invoke(event);
        } else {
            dispatcher.dispatchSubscribe(this, event);
        }
    }

    @Override
    protected Object invoke(Object event) {
        return Reflections.invokeMethod(target, method, event);
    }
}
