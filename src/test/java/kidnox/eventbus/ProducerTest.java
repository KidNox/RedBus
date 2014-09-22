package kidnox.eventbus;

import kidnox.eventbus.test.simple.SimpleProducer;
import kidnox.eventbus.test.simple.SimpleSubscriber;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProducerTest {

    private Bus bus;

    @Before public void setUp() throws Exception {
        bus = Bus.Factory.createDefault();
    }

    @Test public void produceToMultiplySubscribersTest() {
        SimpleProducer simpleProducer = new SimpleProducer();
        SimpleSubscriber subscriber1 = new SimpleSubscriber();
        SimpleSubscriber subscriber2 = new SimpleSubscriber();
        SimpleSubscriber subscriber3 = new SimpleSubscriber();

        bus.register(subscriber1);
        bus.register(subscriber2);
        bus.register(subscriber3);
        bus.register(simpleProducer);

        assertEquals(1, subscriber1.getSubscribedCount());
        assertEquals(1, subscriber2.getSubscribedCount());
        assertEquals(1, subscriber3.getSubscribedCount());
        assertEquals(1, simpleProducer.getProducedCount());
    }

    @Test public void produceToMultiplySubscribersTest2() {
        SimpleProducer simpleProducer = new SimpleProducer();
        SimpleSubscriber subscriber1 = new SimpleSubscriber();
        SimpleSubscriber subscriber2 = new SimpleSubscriber();
        SimpleSubscriber subscriber3 = new SimpleSubscriber();

        bus.register(simpleProducer);
        assertEquals(0, simpleProducer.getProducedCount());
        bus.register(subscriber1);
        assertEquals(1, simpleProducer.getProducedCount());
        bus.register(subscriber2);
        assertEquals(2, simpleProducer.getProducedCount());
        bus.register(subscriber3);
        assertEquals(3, simpleProducer.getProducedCount());

        assertEquals(1, subscriber1.getSubscribedCount());
        assertEquals(1, subscriber2.getSubscribedCount());
        assertEquals(1, subscriber3.getSubscribedCount());

        bus.unregister(simpleProducer);
        bus.register(simpleProducer);

        assertEquals(2, subscriber1.getSubscribedCount());
        assertEquals(2, subscriber2.getSubscribedCount());
        assertEquals(2, subscriber3.getSubscribedCount());
    }

    @Test public void produceToMultiplySubscribersTest3() {
        SimpleProducer simpleProducer = new SimpleProducer();
        SimpleSubscriber subscriber1 = new SimpleSubscriber();
        SimpleSubscriber subscriber2 = new SimpleSubscriber();
        SimpleSubscriber subscriber3 = new SimpleSubscriber();

        bus.register(subscriber1);
        bus.register(subscriber2);
        bus.register(subscriber3);

        bus.register(simpleProducer);
        bus.unregister(simpleProducer);
        bus.register(simpleProducer);

        assertEquals(2, subscriber1.getSubscribedCount());
        assertEquals(2, subscriber2.getSubscribedCount());
        assertEquals(2, subscriber3.getSubscribedCount());
        assertEquals(2, simpleProducer.getProducedCount());
    }

}

