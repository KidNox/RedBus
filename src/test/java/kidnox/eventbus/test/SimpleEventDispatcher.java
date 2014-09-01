package kidnox.eventbus.test;

import kidnox.eventbus.Dispatcher;

public class SimpleEventDispatcher implements Dispatcher {

    Runnable currentEvent;

    public Object getCurrentEvent() {
        return currentEvent;
    }

    @Override public boolean isDispatcherThread() {
        return false;
    }

    @Override public void dispatch(Runnable event) {
        currentEvent = event;
    }
}
