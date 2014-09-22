package kidnox.eventbus.test.simple;

import kidnox.eventbus.*;
import kidnox.eventbus.Handle;
import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.Event2;

@Subscriber
public class SimpleProcessor {

    private volatile Event currentEvent;
    private volatile Event2 returnedEvent;
    private volatile int subscribedCount = 0;

    @Handle
    public Event2 processEvent(Event event) {
        currentEvent = event;
        returnedEvent = new Event2();
        subscribedCount++;
        return returnedEvent;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public Event2 getReturnedEvent() {
        return returnedEvent;
    }

    public int getSubscribedCount() {
        return subscribedCount;
    }
}
