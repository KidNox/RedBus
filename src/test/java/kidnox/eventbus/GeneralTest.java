package kidnox.eventbus;

import kidnox.eventbus.internal.*;
import kidnox.eventbus.internal.bad.BadSubscriber;
import kidnox.eventbus.internal.bad.BadSubscriber2;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GeneralTest {

    private Bus bus;

    @Before public void setUp() throws Exception {
        bus = BusFactory.getDefault();
    }

    @Test public void baseTest() {
        bus.post(new Object());

        SimpleSubscriber subscriber1 = new SimpleSubscriber();
        BadSubscriber badSubscriber = new BadSubscriber();

        bus.register(subscriber1);
        bus.register(badSubscriber);

        Object event = new Object();
        bus.post(event);

        assertNotNull("Subscriber doesn't obtain event", subscriber1.getCurrentEvent());
        assertEquals("Subscriber obtain wrong event", event, subscriber1.getCurrentEvent());
    }

    @Test public void subscriberTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        Object event = new Object();
        bus.post(event);

        bus.unregister(subscriber);

        Object event2 = new Object();
        bus.post(event2);

        assertNotEquals("Subscriber obtain event after unregister", event2, subscriber.getCurrentEvent());
        assertEquals("Subscriber obtain wrong event", event, subscriber.getCurrentEvent());
    }

    @Test public void producerTest() {
        SimpleProducer producer = new SimpleProducer();
        SimpleSubscriber subscriber = new SimpleSubscriber();

        bus.register(producer);
        bus.register(subscriber);

        assertNotNull(subscriber.getCurrentEvent());

        bus.unregister(producer);
        bus.unregister(subscriber);

        assertEquals(subscriber.getSubscribedCount(), 1);

        subscriber = new SimpleSubscriber();

        bus.register(subscriber);
        bus.register(producer);

        assertNotNull(subscriber.getCurrentEvent());
        assertEquals(producer.getProducedCount(), 2);
    }

    @Test public void largeSubscriberTest() {
        LargeSubscriber largeSubscriber = new LargeSubscriber();
        bus.register(largeSubscriber);

        Object object = new Object();
        String string = "test-string";
        Date date = new Date();
        Event event= new Event();

        bus.post(object);
        bus.post(string);
        bus.post(date);
        bus.post(event);

        assertEquals(object, largeSubscriber.getObject());
        assertEquals(string, largeSubscriber.getString());
        assertEquals(date, largeSubscriber.getDate());
        assertEquals(event, largeSubscriber.getEvent());

        bus.post(new Object());
        assertNotEquals(object, largeSubscriber.getObject());
    }

    @Test public void interfaceSubscriberTest() {
        InterfaceSubscriber interfaceSubscriber = new InterfaceSubscriber();
        bus.register(interfaceSubscriber);
        bus.post(new Object());
        assertNotNull(interfaceSubscriber.getCurrentEvent());
    }

    @Test public void registerProducerWithoutSubscribersTest() {
        SimpleProducer producer = new SimpleProducer();
        bus.register(producer);
        assertEquals(producer.getProducedCount(), 0);
        bus.post(new Object());
        bus.register(new Object());
        bus.post(new Object());
        assertEquals(producer.getProducedCount(), 0);

        bus.unregister(producer);
        assertEquals(producer.getProducedCount(), 0);

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        assertEquals(producer.getProducedCount(), 0);
        assertEquals(subscriber.getSubscribedCount(), 0);
        bus.post(new Object());
        assertEquals(subscriber.getSubscribedCount(), 1);
    }

    @Test public void registerSameProducersTest() {
        @Producer
        class Producer1 {
            @Produce public Event produceEvent() {
                return new Event();
            }
        }

        @Producer
        class Producer2 {
            @Produce public Event produceOtherEvent() {
                return new Event();
            }
        }

        bus.register(new Producer1());
        try {
            bus.register(new Producer2());
            fail();
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test public void eventInheritanceTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.post("");
        assertNull("Subscriber obtain wrong event", subscriber.getCurrentEvent());
        bus.unregister(subscriber);

        @Subscriber
        class SubscriberClass {
            String string;

            @Subscribe public void obtainString(String event) {
                string = event;
            }

            @Subscribe public void obtainObject(Object event) {
                fail("wrong event");
            }
        }

        SubscriberClass subscriberClass = new SubscriberClass();
        bus.register(subscriberClass);

        String stringEvent = "stringEvent";
        bus.post(stringEvent);

        assertEquals("string not obtained", stringEvent, subscriberClass.string);
    }

    @Test public void nonPublicMethodsTest() {
        BadSubscriber2 badSubscriber2 = new BadSubscriber2();
        bus.register(badSubscriber2);
        Object o = new Object();
        bus.post(o);
        assertEquals(o, badSubscriber2.getCurrentEvent());
    }
}
