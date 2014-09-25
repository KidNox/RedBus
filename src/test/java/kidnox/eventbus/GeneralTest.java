package kidnox.eventbus;

import kidnox.eventbus.internal.BusException;
import kidnox.eventbus.test.*;
import kidnox.eventbus.test.bad.BadSubscriber;
import kidnox.eventbus.test.bad.BadSubscriber2;
import kidnox.eventbus.test.simple.SimpleProducer;
import kidnox.eventbus.test.simple.SimpleSubscriber;
import kidnox.eventbus.test.simple.SimpleTask;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GeneralTest {

    private Bus bus;

    @Before public void setUp() throws Exception {
        bus = Bus.Factory.createDefault();
    }

    @Test public void baseTest() {
        bus.post(new Object());

        SimpleSubscriber subscriber1 = new SimpleSubscriber();
        BadSubscriber badSubscriber = new BadSubscriber();

        bus.register(subscriber1);
        bus.register(badSubscriber);

        Event event = new Event();
        bus.post(event);

        assertNotNull("Subscriber doesn't obtain event", subscriber1.getCurrentEvent());
        assertEquals("Subscriber obtain wrong event", event, subscriber1.getCurrentEvent());
    }

    @Test public void subscriberTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        Event event = new Event();
        bus.post(event);

        bus.unregister(subscriber);

        Event event2 = new Event();
        bus.post(event2);

        assertNotEquals("Subscriber obtain event after unregister", event2, subscriber.getCurrentEvent());
        assertEquals("Subscriber obtain wrong event", event, subscriber.getCurrentEvent());
    }

    @Test public void subscribersTest() {
        SimpleSubscriber subscriber1 = new SimpleSubscriber();
        SimpleSubscriber subscriber2 = new SimpleSubscriber();
        SimpleSubscriber subscriber3 = new SimpleSubscriber();

        bus.register(subscriber1);
        bus.register(subscriber2);
        bus.register(subscriber3);

        Event event = new Event();
        bus.post(event);

        assertEquals(event, subscriber1.getCurrentEvent());
        assertEquals(event, subscriber2.getCurrentEvent());
        assertEquals(event, subscriber3.getCurrentEvent());

        assertEquals(1, subscriber1.getSubscribedCount());
        assertEquals(1, subscriber2.getSubscribedCount());
        assertEquals(1, subscriber3.getSubscribedCount());
    }

    @Test public void producerTest() {
        SimpleProducer producer = new SimpleProducer();
        SimpleSubscriber subscriber = new SimpleSubscriber();

        bus.register(producer);
        bus.register(subscriber);

        assertNotNull(subscriber.getCurrentEvent());

        bus.unregister(producer);
        bus.unregister(subscriber);

        assertEquals(1, subscriber.getSubscribedCount());

        subscriber = new SimpleSubscriber();

        bus.register(subscriber);
        bus.register(producer);

        assertNotNull(subscriber.getCurrentEvent());
        assertEquals(2, producer.getProducedCount());
    }

    @Test public void eventHandlerTest() {

    }

    @Test public void taskTest() {
        SimpleTask simpleTask = new SimpleTask();
        bus.register(simpleTask);
        assertEquals(1, simpleTask.getExecuteCount());
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
        assertEquals(0, producer.getProducedCount());
        bus.post(new Event());
        bus.register(new Object());
        bus.post(new Object());
        assertEquals(0, producer.getProducedCount());

        bus.unregister(producer);
        assertEquals(0, producer.getProducedCount());

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        assertEquals(0, producer.getProducedCount());
        assertEquals(0, subscriber.getSubscribedCount());
        bus.post(new Event());
        assertEquals(1, subscriber.getSubscribedCount());
    }

    @Test(expected = BusException.class)
    public void registerSameProducersTest() {
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
        bus.register(new Producer2());
    }

    @Test public void eventsNotInheritTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.post(new Event() {});
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
