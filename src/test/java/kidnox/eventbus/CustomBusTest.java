package kidnox.eventbus;

import kidnox.eventbus.annotations.Subscribe;
import kidnox.eventbus.annotations.Subscriber;
import kidnox.eventbus.internal.MutableClassFilterDelegate;
import kidnox.eventbus.internal.SimpleDeadEventHandler;
import kidnox.eventbus.internal.SimpleSubscriber;
import org.junit.Test;

import static org.junit.Assert.*;

public class CustomBusTest {

    Bus bus;

    @Test public void deadEventHandlerTest() {
        SimpleDeadEventHandler deadEventHandler = new SimpleDeadEventHandler();
        bus = BusFactory.builder().withDeadEventHandler(deadEventHandler).create();

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.post(new Object());

        assertNull("onDeadEvent called!, event not null", deadEventHandler.getCurrentEvent());

        bus.unregister(subscriber);
        Object event = new Object();
        bus.post(event);

        assertNotNull("onDeadEvent not called!, event is null", deadEventHandler.getCurrentEvent());

        assertEquals("wrong event", event, deadEventHandler.getCurrentEvent());
    }

    @Test public void classFilerTest() {
        MutableClassFilterDelegate classFilterDelegate = new MutableClassFilterDelegate();
        bus = BusFactory.builder().withClassFilter(classFilterDelegate).create();

        @Subscriber
        class SubscriberClass1 {
            @Subscribe public void obtainEvent(Object event) {
                fail("this class must be filtered");
            }
        }

        classFilterDelegate.set(new ClassFilter() {
            @Override
            public boolean skipClass(Class clazz) {
                return clazz == SubscriberClass1.class;
            }
        });

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.register(new SubscriberClass1());

        Object event = new Object();
        bus.post(event);
        assertEquals("wrong event", event, subscriber.getCurrentEvent());
    }

}
