package kidnox.eventbus;

import kidnox.eventbus.internal.BadSubscriber;
import kidnox.eventbus.internal.InterfaceSubscriber;
import kidnox.eventbus.internal.SimpleSubscriber;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DefaultBusTest {

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
        Object target = new Object();
        bus.register(target);
        bus.post(new Object());
        bus.unregister(target);

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

    @Test public void doubleRegisterTest() {
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        try {
            bus.register(subscriber);
            fail("already registered");
        } catch (RuntimeException ignored) {}
    }

    @Test public void unregisterWhenNotRegisteredTest() {
        try {
            bus.unregister(new Object());
            fail("not registered");
        } catch (RuntimeException ignored) {}

        try {
            bus.unregister(new SimpleSubscriber());
            fail("not registered");
        } catch (RuntimeException ignored) {}
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

    @Test public void classInheritanceTest() {

        @Subscriber
        class Subscriber1 extends SimpleSubscriber {}

        class Subscriber2 extends SimpleSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                fail("class not annotated with @Subscriber!");
            }
        }

        @Subscriber
        class Subscriber3 extends SimpleSubscriber {
            String mEvent;
            @Subscribe public void obtainEvent(String event) {
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
        assertNull("Subscriber obtain wrong event", subscriber2.getCurrentEvent());
        assertEquals("Subscriber obtain wrong event", event, subscriber3.getCurrentEvent());

        bus.post("");
        assertNotNull("Subscriber doesn't obtain event", subscriber3.mEvent);
    }

    @Test public void iSubscriberTest() {
        InterfaceSubscriber interfaceSubscriber = new InterfaceSubscriber();
        bus.register(interfaceSubscriber);
        bus.post(new Object());
        assertNotNull(interfaceSubscriber.getCurrentEvent());
    }


}
