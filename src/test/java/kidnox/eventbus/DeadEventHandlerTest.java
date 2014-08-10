package kidnox.eventbus;

import kidnox.eventbus.internal.Event;
import kidnox.eventbus.internal.SimpleDeadEventHandler;
import kidnox.eventbus.internal.SimpleSubscriber;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DeadEventHandlerTest {

    @Test public void deadEventHandlerTest() {
        SimpleDeadEventHandler deadEventHandler = new SimpleDeadEventHandler();
        Bus bus = Bus.Factory.builder().withDeadEventHandler(deadEventHandler).create();

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.post(new Event());

        assertNull("onDeadEvent called, event not null", deadEventHandler.getCurrentEvent());

        bus.unregister(subscriber);
        Event event = new Event();
        bus.post(event);

        assertNotNull("onDeadEvent not called, event is null", deadEventHandler.getCurrentEvent());

        assertEquals("wrong event", event, deadEventHandler.getCurrentEvent());
    }

}
