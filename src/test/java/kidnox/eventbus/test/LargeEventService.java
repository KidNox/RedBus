package kidnox.eventbus.test;

import kidnox.eventbus.EventService;
import kidnox.eventbus.EventServiceFactory;

@EventServiceFactory
public class LargeEventService {

    @EventService public SimpleSubscriber provideSimpleSubscriber() {
        return new SimpleSubscriber();
    }

    @EventService public SimpleProducer provideSimpleProducer() {
        return  new SimpleProducer();
    }

    @EventService public SimpleProcessor provideSimpleProcessor() {
        return new SimpleProcessor();
    }

    @EventService public LargeSubscriber provideLargeSubscriber() {
        return new LargeSubscriber();
    }

    @EventService public LargeProducer provideLargeProducer() {
        return new LargeProducer();
    }

    @EventService public LargeProcessor provideLargeProcessor() {
        return new LargeProcessor();
    }

}
