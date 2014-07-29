package kidnox.eventbus.impl;

import kidnox.eventbus.Dispatcher;

import java.lang.reflect.Method;

/**beta*/
public final class AsyncEventSubscriber extends EventSubscriber {

    protected AsyncEventSubscriber(Class eventClass, Object target, Method method, Dispatcher dispatcher) {
        super(eventClass, target, method, dispatcher);
    }

    @Override AsyncDispatcher getAsyncDispatcher(Dispatcher dispatcher) {
        return null;
    }

    @Override public void receive(Object event) {
        dispatcher.dispatchSubscribe(this, event);
    }

    @Override public void onUnregister() {
        //unused
    }

}
