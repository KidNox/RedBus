package kidnox.eventbus.test;

import kidnox.eventbus.Handle;
import kidnox.eventbus.Subscriber;

@Subscriber
public class EventHandler {

    volatile Event event;
    volatile Event2 event2;

    @Handle public Event2 handleEvent(Event event) {
        this.event = event;
        this.event2 = new Event2();
        return event2;
    }

    public Event getEvent() {
        return event;
    }

    public Event2 getEvent2() {
        return event2;
    }
}
