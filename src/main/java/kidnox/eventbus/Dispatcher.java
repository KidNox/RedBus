package kidnox.eventbus;

import kidnox.annotations.NotNull;
import kidnox.eventbus.impl.EventSubscriber;

public interface Dispatcher {
    void dispatchSubscribe(EventSubscriber subscriber, Object event);


    public interface Factory {
        public Dispatcher getDispatcher(@NotNull String subscriberName);
    }
}
