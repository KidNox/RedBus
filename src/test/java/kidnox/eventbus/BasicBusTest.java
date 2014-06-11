package kidnox.eventbus;

import kidnox.eventbus.annotations.Subscribe;
import kidnox.eventbus.annotations.Subscriber;
import kidnox.eventbus.internal.BadSubscriber;
import kidnox.eventbus.internal.SimpleSubscriber;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class BasicBusTest {

    private Bus bus;

    @Before public void setUp() throws Exception {
        bus = BusFactory.getDefault();
    }


    @Test public void baseTest() {
        SimpleSubscriber subscriber1 = new SimpleSubscriber();
        BadSubscriber badSubscriber = new BadSubscriber();

        bus.register(subscriber1);
        bus.register(badSubscriber);

        Object event = new Object();
        bus.post(event);

        assertNotNull("Subscriber doesn't obtain event", subscriber1.getCurrentEvent());
        assertEquals("Subscriber obtain wrong event", event, subscriber1.getCurrentEvent());
    }

    @Test public void unregisterTest() {
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

    @Test public void eventInheritanceTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.post("");
        assertNull("Subscriber obtain wrong event", subscriber.getCurrentEvent());
    }

    @Test public void classInheritanceTest() {

        class Subscriber1 extends SimpleSubscriber {}

        class Subscriber2 extends SimpleSubscriber {
            @Subscribe
            public void obtainEvent(Object event) {
                fail("class not annotated with @Subscriber!");
            }
        }

        @Subscriber
        class Subscriber3 extends SimpleSubscriber {
            Object mEvent;
            @Subscribe public void obtainEvent(Object event) {
                mEvent = event;
            }
        }

        Subscriber1 subscriber1 = new Subscriber1();
        Subscriber2 subscriber2 = new Subscriber2();
        Subscriber3 subscriber3 = new Subscriber3();

        bus.register(subscriber1);
        bus.register(subscriber2);
        bus.register(subscriber3);

        Object event = new Object();
        bus.post(event);

        assertEquals("Subscriber obtain wrong event", event, subscriber1.getCurrentEvent());
        assertEquals("Subscriber obtain wrong event", event, subscriber2.getCurrentEvent());
        assertEquals("Subscriber obtain wrong event", event, subscriber3.getCurrentEvent());

        assertNotNull("Subscriber doesn't obtain event", subscriber3.getCurrentEvent());
    }



}
