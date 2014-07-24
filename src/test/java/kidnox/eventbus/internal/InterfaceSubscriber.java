package kidnox.eventbus.internal;

import kidnox.eventbus.Subscribe;
import kidnox.eventbus.Subscriber;

@Subscriber
public class InterfaceSubscriber extends AbsAsyncSubscriber implements ISubscriber {

    @Subscribe
    @Override public void obtainEvent(Object event) {
        currentEvent = event;
    }
}
