package kidnox.eventbus.internal;

import kidnox.eventbus.EventDispatcher;

import java.lang.reflect.InvocationTargetException;

public final class AsyncElement extends Element {

    public final EventDispatcher eventDispatcher;

    //Mutable, but not volatile.
    //Unregister should be called from eventDispatcher thread, otherwise invoke may be called after unregister.
    private boolean valid = true;

    public AsyncElement(ElementInfo elementInfo, Object target, EventDispatcher dispatcher) {
        super(elementInfo, target);
        this.eventDispatcher = dispatcher;
    }

    public void onUnregister() {
        valid = false;
    }

    public boolean isValid() {
        return valid;
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
