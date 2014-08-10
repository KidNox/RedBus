package kidnox.eventbus.internal;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.elements.EventProducer;
import kidnox.eventbus.elements.EventSubscriber;

public class SimpleEventDispatcher implements EventDispatcher {

    EventSubscriber currentSubscriber;
    EventProducer currentProducer;
    Object currentEvent;

    @Override public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
        currentSubscriber = subscriber;
        currentEvent = event;
    }

    @Override public void dispatchProduce(EventProducer eventProducer, EventSubscriber eventSubscriber) {
        currentProducer = eventProducer;
        currentSubscriber = eventSubscriber;
    }

    public EventSubscriber getCurrentSubscriber() {
        return currentSubscriber;
    }

    public Object getCurrentEvent() {
        return currentEvent;
    }
}
