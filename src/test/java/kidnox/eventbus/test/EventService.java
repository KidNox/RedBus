package kidnox.eventbus.test;

import kidnox.eventbus.EventServiceFactory;

@EventServiceFactory
public class EventService {

    @kidnox.eventbus.EventService public SimpleSubscriber createSimpleSubscriber() {
        return new SimpleSubscriber();
    }

}
