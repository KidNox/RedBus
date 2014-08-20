package kidnox.eventbus.elements;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.ExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class EventSubscriber extends Element {

    public final EventDispatcher eventDispatcher;

    //Mutable, but not volatile.
    //Unregister should be called from eventDispatcher thread, otherwise invoke may be called after unregister.
    private boolean valid = true;

    public EventSubscriber(Class eventClass, Object target, Method method, EventDispatcher dispatcher) {
        super(eventClass, target, method);
        this.eventDispatcher = dispatcher;
    }

//    public void receive(Object event) {
//        eventDispatcher.dispatchSubscribe(this, event);
//    }

    public void onUnregister() {
        valid = false;
    }

    @Override public Object invoke(Object... event) throws InvocationTargetException {
        if(valid) return super.invoke(event);
        else return null; //Subscriber already unregistered here
    }
}
