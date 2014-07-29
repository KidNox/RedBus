package kidnox.eventbus.impl;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.Element;
import kidnox.eventbus.utils.Utils;

import java.lang.reflect.Method;

public class EventSubscriber extends Element {

    final Dispatcher dispatcher;
    final AsyncDispatcher asyncDispatcher;

    //Mutable, but not volatile.
    //Unregister should be called from invocation thread, otherwise invoke may be called after unregister.
    private boolean valid = true;

    EventSubscriber(Class eventClass, Object target, Method method, Dispatcher dispatcher) {
        super(eventClass, target, method);
        this.dispatcher = dispatcher;
        asyncDispatcher = getAsyncDispatcher(dispatcher);
    }

    AsyncDispatcher getAsyncDispatcher(Dispatcher dispatcher) {
        return dispatcher instanceof AsyncDispatcher ? (AsyncDispatcher) dispatcher : null;
    }

    public void receive(Object event) {
        if (asyncDispatcher != null && asyncDispatcher.inCurrentThread()) {
            invoke(event);
        } else {
            dispatcher.dispatchSubscribe(this, event);
        }
    }

    public void onUnregister() {
        valid = false;
    }

    @Override public Object invoke(Object event) {
        if(valid) return Utils.invokeMethod(target, method, event);
        else return null; //Subscriber already unregistered here
    }
}
