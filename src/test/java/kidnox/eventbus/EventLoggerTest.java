package kidnox.eventbus;

import kidnox.eventbus.impl.BusImpl;
import kidnox.eventbus.internal.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class EventLoggerTest {

    EventLoggerImpl eventLogger;
    Bus bus;

    @Before public void setUp() {
        eventLogger = new EventLoggerImpl();
        bus = Bus.Factory.builder().withEventLogger(eventLogger).create();
    }

    @Test public void elementsTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.post(new Event());

        assertNotNull(eventLogger.getWhat());
        assertNotNull(eventLogger.getEvent());
        assertNotNull(eventLogger.getElement());
    }

    @Test public void nullElementTest() {
        bus.post(new Object());
        assertNull(eventLogger.getElement());
    }

    @Test public void postActionTest() {
        bus.post(new Event());
        assertEquals(BusImpl.POST, eventLogger.getWhat());
    }

    @Test public void produceActionTest() {
        SimpleProducer producer = new SimpleProducer();
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(producer);
        bus.register(subscriber);

        assertEquals(BusImpl.PRODUCE, eventLogger.getWhat());
    }

    @Test public  void interceptActionTest() {
        EventInterceptor interceptor = new EventInterceptor();

    }

}
