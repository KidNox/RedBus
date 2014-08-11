package kidnox.eventbus.test.exceptions;

import kidnox.eventbus.Subscribe;
import kidnox.eventbus.Subscriber;
import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.Event2;

@Subscriber
public class ThrowingSubscriber {

    private volatile Event event;
    private volatile Event2 event2;

    @Subscribe public void obtainEvent(Event event) {
        this.event = event;
        throw new RuntimeException();
    }

    @Subscribe public void obtainEvent(Event2 event) throws TestException {
        this.event2 = event;
        throw new TestException();

    }

    public Event getEvent() {
        return event;
    }

    public Event2 getEvent2() {
        return event2;
    }
}
