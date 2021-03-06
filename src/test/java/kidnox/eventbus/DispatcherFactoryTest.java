package kidnox.eventbus;

import kidnox.eventbus.internal.element.AsyncElement;
import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.PackageLocalProvider;
import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.simple.SimpleEventDispatcher;
import kidnox.eventbus.test.simple.SimpleSubscriber;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class DispatcherFactoryTest {

    @Test public void dispatcherFactoryTest() {
        AsyncBus bus = (AsyncBus) Bus.Factory.builder().withEventDispatcherFactory(new Dispatcher.Factory() {
            @Override public Dispatcher getDispatcher(String name) {
                fail("must not be called");
                return null;
            }
        }).build();

        Object target = new Object();
        bus.post(new Object());
        bus.register(target);
        bus.post(new Object());
        bus.unregister(target);

        final SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        bus = (AsyncBus) Bus.Factory.builder().withEventDispatcherFactory(new Dispatcher.Factory() {
            @Override public Dispatcher getDispatcher(String dispatcherName) {
                return dispatcher;
            }
        }).build();

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        Set<AsyncElement> eventSubscribers = PackageLocalProvider.getSubscribers(bus, Event.class);
        assertNotNull("valid subscriber without event subscribers", eventSubscribers);

        Event event = new Event();
        bus.post(event);

        assertNotNull("dispatch not called, event null", dispatcher.getCurrentEvent());
    }

}
