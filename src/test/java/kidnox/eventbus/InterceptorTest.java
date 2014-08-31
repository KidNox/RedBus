package kidnox.eventbus;

import kidnox.eventbus.test.*;
import kidnox.eventbus.util.EventTypeInterceptor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InterceptorTest {

    EventTypeCountInterceptor interceptor;
    Bus bus;

    @Before public void setUp() {
        interceptor = new EventTypeCountInterceptor(Event.class);
        bus = Bus.Factory.builder().withInterceptor(interceptor).create();
    }

    @Test public void interceptPostTest() {
        EventsSubscriber eventsSubscriber = new EventsSubscriber();
        bus.register(eventsSubscriber);

        bus.post(new Event());
        bus.post(new Event2());

        assertNull(eventsSubscriber.getEvent());
        assertNotNull(eventsSubscriber.getEvent2());
        assertEquals(2, interceptor.getCallCount());
    }

    @Test public void interceptProduce() {
        EventsProducer eventsProducer = new EventsProducer();
        EventsSubscriber eventsSubscriber = new EventsSubscriber();
        bus.register(eventsProducer);
        bus.register(eventsSubscriber);

        assertEquals(1, eventsProducer.getProducedEventsCount());
        assertEquals(1, eventsProducer.getProducedEvents2Count());
        assertNull(eventsSubscriber.getEvent());
        assertNotNull(eventsSubscriber.getEvent2());
        assertEquals(2, interceptor.getCallCount());
    }

}
