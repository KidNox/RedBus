package kidnox.eventbus.internal;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.impl.EventSubscriber;

public class MutableDispatcherDelegate implements Dispatcher {

    Dispatcher internal;

    @Override
    public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
        if(internal != null) {
            internal.dispatchSubscribe(subscriber, event);
        }
    }

    public void set(Dispatcher dispatcher) {
        internal = dispatcher;
    }

}
