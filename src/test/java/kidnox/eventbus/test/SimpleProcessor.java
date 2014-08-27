package kidnox.eventbus.test;

import kidnox.eventbus.*;
import kidnox.eventbus.Handle;

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
