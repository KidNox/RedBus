package kidnox.eventbus;

import kidnox.eventbus.internal.element.AsyncElement;
import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.PackageLocalProvider;
import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.SimpleEventDispatcher;
import kidnox.eventbus.test.SimpleSubscriber;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class EventDispatcherFactoryTest {

    @Test public void dispatcherFactoryTest() {
        AsyncBus bus = (AsyncBus) Bus.Factory.builder().withEventDispatcherFactory(new EventDispatcher.Factory() {
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
        bus = (AsyncBus) Bus.Factory.builder().withEventDispatcherFactory(new EventDispatcher.Factory() {
            @Override public EventDispatcher getDispatcher(String dispatcherName) {
                return dispatcher;
            }
        }).create();

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        Set<AsyncElement> eventSubscribers = PackageLocalProvider.getSubscribers(bus, Event.class);
        assertNotNull("valid subscriber without event subscribers", eventSubscribers);

        Event event = new Event();
        bus.post(event);

        assertNotNull("dispatch not called, event null", dispatcher.getCurrentEvent());
    }

}
