package kidnox.eventbus.elements;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.impl.AsyncDispatcher;
import kidnox.eventbus.utils.Utils;

import java.lang.reflect.Method;

/**beta*/
public final class AsyncEventSubscriber extends EventSubscriber {

    private volatile boolean valid = true;

    public AsyncEventSubscriber(Class eventClass, Object target, Method method, Dispatcher dispatcher) {
        super(eventClass, target, method, dispatcher);
    }

    @Override AsyncDispatcher getAsyncDispatcher(Dispatcher dispatcher) {
        return null;
    }

    @Override public void receive(Object event) {
        dispatcher.dispatchSubscribe(this, event);
    }

    public void onUnregister() {
        valid = false;
    }

    @Override public Object invoke(Object event) {
        if(valid) return Utils.invokeMethod(target, method, event);
        else return null; //Subscriber already unregistered here
    }

}
