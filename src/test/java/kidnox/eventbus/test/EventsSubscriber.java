package kidnox.eventbus.test;

import kidnox.eventbus.Subscribe;
import kidnox.eventbus.Subscriber;

@Subscriber
public class EventsSubscriber {

    private volatile Event event;
    private volatile Event2 event2;

    @Subscribe public void obtainEvent(Event event) {
        this.event = event;
    }

    @Subscribe public void obtainEvent(Event2 event) {
        this.event2 = event;
    }

    public Event getEvent() {
        return event;
    }

    public Event2 getEvent2() {
        return event2;
    }
}
