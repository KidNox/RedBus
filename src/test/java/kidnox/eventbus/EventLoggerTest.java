package kidnox.eventbus;

import kidnox.eventbus.internal.AsyncElement;
import kidnox.eventbus.impl.AsyncBus;
import kidnox.eventbus.util.EventInterceptor;
import kidnox.eventbus.test.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class EventLoggerTest {

    public static final Event EVENT = new Event();

    EventLoggerImpl eventLogger;
    Bus bus;

    @Before public void setUp() {
        eventLogger = new EventLoggerImpl();
        bus = Bus.Factory.builder().withEventLogger(eventLogger).create();
    }

    @Test public void elementsTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.post(EVENT);

        assertNotNull(eventLogger.getWhat());
        assertNotNull(eventLogger.getEvent());
        assertNotNull(eventLogger.getTarget());
    }

    @Test public void nullElementTest() {
        bus.post(EVENT);
        assertNull(eventLogger.getTarget());
    }

    @Test public void postActionTest() {
        bus.post(EVENT);
        assertEquals(AsyncBus.POST, eventLogger.getWhat());
    }

    @Test public void produceActionTest() {
        SimpleProducer producer = new SimpleProducer();
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(producer);
        bus.register(subscriber);

        assertEquals(AsyncBus.PRODUCE, eventLogger.getWhat());
        assertNotNull(eventLogger.getEvent());
    }

    @Test public void nullProduceLogTest() {
        MutableProducer mutableProducer = new MutableProducer();
        mutableProducer.setEvent(null);
        SimpleSubscriber simpleSubscriber = new SimpleSubscriber();
        bus.register(mutableProducer);
        bus.register(simpleSubscriber);

        assertEquals(AsyncBus.PRODUCE, eventLogger.getWhat());
        assertNull(eventLogger.getEvent());
        assertNotNull(eventLogger.getTarget());
    }

    @Test public  void interceptActionTest() {
        bus = Bus.Factory.builder().withEventLogger(eventLogger)
                .withInterceptor(new EventInterceptor(Event.class)).create();
        bus.register(new SimpleSubscriber());
        bus.post(EVENT);

        assertEquals(AsyncBus.INTERCEPT, eventLogger.getWhat());
        assertNotNull(eventLogger.getEvent());
    }

    @SuppressWarnings("unchecked")
    @Test public void targetsTest() {
        SimpleSubscriber subscriber1 = new SimpleSubscriber();
        SimpleSubscriber subscriber2 = new SimpleSubscriber();
        bus.register(subscriber1);
        bus.register(subscriber2);

        bus.post(EVENT);
        assertTrue(eventLogger.getTarget() instanceof Set);
        Set<AsyncElement> set = (Set<AsyncElement>) eventLogger.getTarget();
        assertEquals(2, set.size());
    }

    @SuppressWarnings("unchecked")
    @Test public void targetsTest2() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        SimpleProducer producer = new SimpleProducer();
        bus.register(subscriber);
        bus.register(producer);

        assertTrue(eventLogger.getTarget() instanceof Set);
        Set<AsyncElement> set = (Set<AsyncElement>) eventLogger.getTarget();
        assertEquals(1, set.size());
    }

    @Test public void targetTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        SimpleProducer producer = new SimpleProducer();
        bus.register(producer);
        bus.register(subscriber);

        assertTrue(eventLogger.getTarget() instanceof AsyncElement);
    }

}
