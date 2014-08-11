package kidnox.eventbus;

import kidnox.eventbus.elements.EventSubscriber;
import kidnox.eventbus.impl.BusImpl;
import kidnox.eventbus.impl.PackageLocalProvider;
import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.SimpleEventDispatcher;
import kidnox.eventbus.test.SimpleSubscriber;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EventDispatcherFactoryTest {

    @Test public void dispatcherFactoryTest() {
        BusImpl bus = (BusImpl) Bus.Factory.builder().withDispatcherFactory(new EventDispatcher.Factory() {
            @Override public EventDispatcher getDispatcher(String name) {
                fail("must not be called");//TODO
                return null;
            }
        }).create();

        Object target = new Object();
        bus.post(new Object());
        bus.register(target);
        bus.post(new Object());
        bus.unregister(target);

        final SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        bus = (BusImpl) Bus.Factory.builder().withDispatcherFactory(new EventDispatcher.Factory() {
            @Override public EventDispatcher getDispatcher(String dispatcherName) {
                return dispatcher;
            }
        }).create();

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        List<EventSubscriber> eventSubscribers = PackageLocalProvider.getSubscribers(bus, subscriber);
        assertNotNull("valid subscriber without event subscribers", eventSubscribers);

        Event event = new Event();
        bus.post(event);

        assertNotNull("dispatchSubscribe not called, event null", dispatcher.getCurrentEvent());
        assertEquals("wrong event in dispatcher", event, dispatcher.getCurrentEvent());
        assertTrue("wrong event subscriber in dispatcher", eventSubscribers.contains(dispatcher.getCurrentSubscriber()));
    }

}
