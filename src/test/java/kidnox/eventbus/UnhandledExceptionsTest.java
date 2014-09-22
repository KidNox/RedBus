package kidnox.eventbus;

import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.Event2;
import kidnox.eventbus.test.EventsSubscriber;
import kidnox.eventbus.test.simple.SimpleSubscriber;
import kidnox.eventbus.test.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnhandledExceptionsTest {

    Bus bus;

    @Before public void setUp() {
        bus = Bus.Factory.createDefault();
    }

    @Test(expected = RuntimeException.class)
    public void unhandledSubscriberExceptionTest() {
        bus.register(new ThrowingSubscriber());
        bus.post(new Event());
    }

    @Test public void unhandledSubscriberExceptionTest2() {
        bus.register(new ThrowingSubscriber());
        try {
            bus.post(new Event2());
            fail();
        } catch (RuntimeException ex) {
            assertNotNull(ex.getCause());
            assertTrue(ex.getCause() instanceof TestException);
        }
    }

    @Test public void unhandledRegUnregMethodsTest() {
        ThrowingSubscriber2 throwingSubscriber2 = new ThrowingSubscriber2();
        try {
            bus.register(throwingSubscriber2);
            fail();
        } catch (RuntimeException ignored) {}
        try {
            bus.unregister(throwingSubscriber2);
            fail();
        } catch (RuntimeException ignored) {}
        assertEquals(1, throwingSubscriber2.getOnRegisterCount());
        assertEquals(1, throwingSubscriber2.getOnUnregisterCount());
    }

    @Test(expected = RuntimeException.class)
    public void unhandledProducerExceptionTest() {
        bus.register(new ThrowingProducer());
        bus.register(new SimpleSubscriber());
    }

    @Test public void unhandledProducerExceptionTest2() {
        bus.register(new ThrowingProducer2());
        try {
            bus.register(new EventsSubscriber());
            fail();
        } catch (RuntimeException ex) {
            assertNotNull(ex.getCause());
            assertTrue(ex.getCause() instanceof TestException);
        }
    }

}
