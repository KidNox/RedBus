package kidnox.eventbus.internal;

import kidnox.eventbus.DeadEventHandler;

public class SimpleDeadEventHandler implements DeadEventHandler{

    Object currentEvent;

    @Override
    public void onDeadEvent(Object event) {
        currentEvent = event;
    }

    public Object getCurrentEvent() {
        return currentEvent;
    }
}
