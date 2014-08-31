package kidnox.eventbus.test;

import kidnox.eventbus.EventDispatcher;

public class SimpleEventDispatcher implements EventDispatcher {

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
