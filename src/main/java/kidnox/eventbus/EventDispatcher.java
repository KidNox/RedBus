package kidnox.eventbus;

import kidnox.eventbus.elements.EventSubscriber;

public interface EventDispatcher {

    String MAIN     = "main";
    String WORKER   = "worker";

    boolean isDispatcherThread();

    void dispatch(Runnable event);

    //void dispatchSubscribe(EventSubscriber subscriber, Object event);

    //TODO figure out about lazy dispatchers
    interface Factory {//TODO move to separate class
        EventDispatcher getDispatcher(String name);
    }

}
