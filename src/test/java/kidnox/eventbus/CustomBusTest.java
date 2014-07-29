package kidnox.eventbus;

import kidnox.eventbus.impl.BusImpl;
import kidnox.eventbus.impl.EventSubscriber;
import kidnox.eventbus.impl.PackageLocalProvider;
import kidnox.eventbus.internal.*;
import org.junit.Test;

import java.util.List;

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

    @Test public void dispatcherFactoryTest() {
        BusImpl bus = (BusImpl) BusFactory.builder().withDispatcherFactory(new Dispatcher.Factory() {
            @Override public Dispatcher getDispatcher(String name) {
                fail("must not be called");
                return null;
            }
        }).create();

        Object target = new Object();
        bus.post(new Object());
        bus.register(target);
        bus.post(new Object());
        bus.unregister(target);

        final SimpleDispatcher dispatcher = new SimpleDispatcher();
        BusFactory.Builder builder = BusFactory.builder();
        bus = (BusImpl) builder.withDispatcherFactory(new Dispatcher.Factory() {
            @Override public Dispatcher getDispatcher(String dispatcherName) {
                return dispatcher;
            }
        }).create();

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        List<EventSubscriber> eventSubscribers = PackageLocalProvider.getSubscribers(bus, subscriber);
        assertNotNull("valid subscriber without event subscribers", eventSubscribers);

        Object event = new Object();
        bus.post(event);

        assertNotNull("dispatchSubscribe not called, event null", dispatcher.getCurrentEvent());
        assertEquals("wrong event in dispatcher", event, dispatcher.getCurrentEvent());
        assertTrue("wrong event subscriber in dispatcher", eventSubscribers.contains(dispatcher.getCurrentSubscriber()));
    }

    @Test public void eventLoggerTest() {
        final EventLoggerImpl logger = new EventLoggerImpl();
        bus = BusFactory.builder().withEventLogger(logger).create();

        SimpleSubscriber badSubscriber2 = new SimpleSubscriber();
        bus.register(badSubscriber2);

        bus.post(new Object());

        assertNotNull(logger.getEvent());
    }

}
