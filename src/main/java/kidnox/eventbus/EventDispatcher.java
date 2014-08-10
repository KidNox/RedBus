package kidnox.eventbus;

import kidnox.eventbus.elements.EventProducer;
import kidnox.eventbus.elements.EventSubscriber;

public interface EventDispatcher {

    String MAIN     = "main";
    String WORKER   = "worker";

    void dispatchSubscribe(EventSubscriber subscriber, Object event);

    void dispatchProduce(EventProducer eventProducer, EventSubscriber eventSubscriber);

    interface Factory {
        EventDispatcher getDispatcher(String name);
    }

}
