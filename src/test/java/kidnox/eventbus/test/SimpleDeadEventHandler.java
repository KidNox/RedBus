package kidnox.eventbus.test;

import kidnox.eventbus.DeadEventHandler;

public class SimpleDeadEventHandler implements DeadEventHandler{

    Object currentEvent;

    @Override public void onDeadEvent(Object event) {
        currentEvent = event;
    }

    public Object getCurrentEvent() {
        return currentEvent;
    }
}
