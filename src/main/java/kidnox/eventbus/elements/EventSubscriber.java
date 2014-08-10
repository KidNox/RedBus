package kidnox.eventbus.elements;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.ExceptionHandler;

import java.lang.reflect.Method;

public class EventSubscriber extends Element {

    final EventDispatcher eventDispatcher;

    //Mutable, but not volatile.
    //Unregister should be called from invocation thread, otherwise invoke may be called after unregister.
    private boolean valid = true;

    public EventSubscriber(Class eventClass, Object target, Method method,
                           EventDispatcher dispatcher, ExceptionHandler exceptionHandler) {
        super(eventClass, target, method, exceptionHandler);
        this.eventDispatcher = dispatcher;
    }

    public void receive(Object event) {
        eventDispatcher.dispatchSubscribe(this, event);
    }

    public void onUnregister() {
        valid = false;
    }

    @Override public Object invoke(Object event) {
        if(valid) return invokeMethod(event);
        else return null; //Subscriber already unregistered here
    }
}
