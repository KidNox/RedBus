package kidnox.eventbus;

import kidnox.annotations.Nonnull;
import kidnox.common.utils.Reflections;

import java.lang.reflect.Method;


public class EventSubscriber extends Element {

    protected final Dispatcher dispatcher;
    protected final AsyncDispatcher asyncDispatcher;

    protected EventSubscriber(@Nonnull Class eventClass, @Nonnull Object target,
                              @Nonnull Method method, @Nonnull Dispatcher dispatcher) {
        super(eventClass, target, method);
        this.dispatcher = dispatcher;

        if (dispatcher instanceof AsyncDispatcher) {
            asyncDispatcher = (AsyncDispatcher) dispatcher;
        } else {
            asyncDispatcher = null;
        }
    }

    protected void invokeSubscribe(Object event) {
        Reflections.invokeMethod(target, method, event);
    }

    protected void dispatch(Object event) {
        if (asyncDispatcher != null && asyncDispatcher.isCurrentThread()) {
            invokeSubscribe(event);
        } else {
            dispatcher.dispatchSubscribe(this, event);
        }
    }

}
