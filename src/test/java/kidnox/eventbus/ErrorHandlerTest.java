package kidnox.eventbus;

import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.Event2;
import kidnox.eventbus.test.EventsSubscriber;
import kidnox.eventbus.test.SimpleSubscriber;
import kidnox.eventbus.test.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ErrorHandlerTest {

    Bus bus;
    DefinedErrorHandler exceptionHandler;

    @Before public void setUp() {
        exceptionHandler = new DefinedErrorHandler(true);
        bus = Bus.Factory.builder().withExceptionHandler(exceptionHandler).create();
    }

    @Test(expected = RuntimeException.class)
    public void unhandledExceptionTest() {
        exceptionHandler = new DefinedErrorHandler(false);
        bus = Bus.Factory.builder().withExceptionHandler(exceptionHandler).create();
        bus.register(new ThrowingSubscriber());
        bus.post(new Event());
    }

    @Test public void subscriberCatchRuntimeExceptionTest() {
        ThrowingSubscriber throwingSubscriber = new ThrowingSubscriber();
        bus.register(throwingSubscriber);
        bus.post(new Event());

        assertNotNull(throwingSubscriber.getEvent());
        assertNotNull(exceptionHandler.getEvent());
        assertNotNull(exceptionHandler.getTarget());
        assertNotNull(exceptionHandler.getThrowable());
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test public void subscriberCatchExceptionTest() {
        bus.register(new ThrowingSubscriber());
        bus.post(new Event2());
        assertTrue(exceptionHandler.getThrowable() instanceof TestException);
    }

    @Test public void produceRuntimeExceptionTest() {
        ThrowingProducer throwingProducer = new ThrowingProducer();
        SimpleSubscriber simpleSubscriber = new SimpleSubscriber();
        bus.register(simpleSubscriber);
        bus.register(throwingProducer);

        assertEquals(1, throwingProducer.getProducedCount());
        assertEquals(0, simpleSubscriber.getSubscribedCount());
        assertNull(exceptionHandler.getEvent());
        assertNotNull(exceptionHandler.getTarget());
        assertNotNull(exceptionHandler.getThrowable());
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test public void producerCatchExceptionTest() {
        bus.register(new ThrowingProducer2());
        bus.register(new EventsSubscriber());
        assertTrue(exceptionHandler.getThrowable() instanceof TestException);
    }

    @Test public void produceSubscribeExceptionsTest() {
        ThrowingSubscriber throwingSubscriber = new ThrowingSubscriber();
        ThrowingProducer throwingProducer = new ThrowingProducer();
        ThrowingProducer2 throwingProducer2 = new ThrowingProducer2();

        bus.register(throwingSubscriber);
        bus.register(throwingProducer);

        assertEquals(1, exceptionHandler.getCatchCount());
        bus.register(throwingProducer2);
        assertEquals(2, exceptionHandler.getCatchCount());
    }

}
