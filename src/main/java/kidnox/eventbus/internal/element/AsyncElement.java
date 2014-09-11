package kidnox.eventbus.internal.element;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.Utils;

import java.lang.reflect.InvocationTargetException;

public final class AsyncElement extends Element {

    public final Dispatcher dispatcher;

    //Mutable, but not volatile.
    //Unregister should be called from eventDispatcher thread, otherwise invoke may be called after unregister.
    private boolean valid = true;

    public AsyncElement(Object target, ElementInfo elementInfo, Dispatcher dispatcher) {
        super(elementInfo, target);
        this.dispatcher = dispatcher;
    }

    public void onUnregister() {
        valid = false;
    }

    @Override public Object invoke(Object... event) throws InvocationTargetException {
        if(valid) return super.invoke(event);
        if (!Utils.isNullOrEmpty(event)) {
            //unregistered subscriber return event for dead event handler
            return event[0];
        }
        else return null; //Producer already unregistered here
    }
}
