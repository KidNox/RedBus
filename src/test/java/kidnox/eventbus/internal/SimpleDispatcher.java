package kidnox.eventbus.internal;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.impl.EventSubscriber;

public class SimpleDispatcher implements Dispatcher {

    EventSubscriber currentSubscriber;
    Object currentEvent;

    @Override public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
        currentSubscriber = subscriber;
        currentEvent = event;
    }

    public EventSubscriber getCurrentSubscriber() {
        return currentSubscriber;
    }

    public Object getCurrentEvent() {
        return currentEvent;
    }
}
