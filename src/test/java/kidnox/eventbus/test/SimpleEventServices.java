package kidnox.eventbus.test;

import kidnox.eventbus.EventService;
import kidnox.eventbus.EventServiceFactory;

@EventServiceFactory
public class SimpleEventServices {

    @EventService public SimpleSubscriber provideSimpleSubscriber() {
        return new SimpleSubscriber();
    }

    @EventService public SimpleProducer provideSimpleProducer() {
        return  new SimpleProducer();
    }

}
