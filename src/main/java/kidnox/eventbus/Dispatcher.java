package kidnox.eventbus;

import kidnox.eventbus.impl.EventSubscriber;

public interface Dispatcher {

    String MAIN     = "main";
    String WORKER   = "worker";

    void dispatchSubscribe(EventSubscriber subscriber, Object event);


    interface Factory {
        Dispatcher getDispatcher(String name);
    }

}
