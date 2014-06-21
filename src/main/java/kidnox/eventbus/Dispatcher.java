package kidnox.eventbus;

import kidnox.eventbus.impl.EventSubscriber;

public interface Dispatcher {
    void dispatchSubscribe(EventSubscriber subscriber, Object event);
}
