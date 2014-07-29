package kidnox.eventbus.internal;

import kidnox.eventbus.Subscribe;
import kidnox.eventbus.Subscriber;

@Subscriber
public class SimpleSubscriber {

    private volatile Event currentEvent;
    private volatile int subscribedCount = 0;

    @Subscribe public void obtainEvent(Event event) {
        subscribedCount++;
        currentEvent = event;
    }

    public Object getCurrentEvent() {
        return currentEvent;
    }

    public int getSubscribedCount() {
        return subscribedCount;
    }
}
