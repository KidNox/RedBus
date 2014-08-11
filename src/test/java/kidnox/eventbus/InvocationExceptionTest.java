package kidnox.eventbus;

import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.Event2;
import kidnox.eventbus.test.EventsSubscriber;
import kidnox.eventbus.test.SimpleSubscriber;
import kidnox.eventbus.test.exceptions.TestException;
import kidnox.eventbus.test.exceptions.ThrowingProducer;
import kidnox.eventbus.test.exceptions.ThrowingProducer2;
import kidnox.eventbus.test.exceptions.ThrowingSubscriber;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InvocationExceptionTest {

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
