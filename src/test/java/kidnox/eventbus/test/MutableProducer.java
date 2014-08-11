package kidnox.eventbus.test;

import kidnox.eventbus.Producer;

@Producer
public class MutableProducer extends SimpleProducer {

    volatile Event event;

    @Override protected Event obtainEvent() {
        return event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
