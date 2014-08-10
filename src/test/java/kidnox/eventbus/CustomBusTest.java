package kidnox.eventbus;

import kidnox.eventbus.impl.BusImpl;
import kidnox.eventbus.elements.EventSubscriber;
import kidnox.eventbus.impl.PackageLocalProvider;
import kidnox.eventbus.internal.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CustomBusTest {

    Bus bus;

    @Test public void deadEventHandlerTest() {
        SimpleDeadEventHandler deadEventHandler = new SimpleDeadEventHandler();
        bus = Bus.Factory.builder().withDeadEventHandler(deadEventHandler).create();

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        bus.post(new Event());

        assertNull("onDeadEvent called!, event not null", deadEventHandler.getCurrentEvent());

        bus.unregister(subscriber);
        Event event = new Event();
        bus.post(event);

        assertNotNull("onDeadEvent not called!, event is null", deadEventHandler.getCurrentEvent());

        assertEquals("wrong event", event, deadEventHandler.getCurrentEvent());
    }

    @Test public void dispatcherFactoryTest() {
        BusImpl bus = (BusImpl) Bus.Factory.builder().withDispatcherFactory(new Dispatcher.Factory() {
            @Override public Dispatcher getDispatcher(String name) {
                fail("must not be called");//TODO
                return null;
            }
        }).create();

        Object target = new Object();
        bus.post(new Object());
        bus.register(target);
        bus.post(new Object());
        bus.unregister(target);

        final SimpleDispatcher dispatcher = new SimpleDispatcher();
        bus = (BusImpl) Bus.Factory.builder().withDispatcherFactory(new Dispatcher.Factory() {
            @Override public Dispatcher getDispatcher(String dispatcherName) {
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

    @Test public void eventLoggerTest() {
        final EventLoggerImpl logger = new EventLoggerImpl();
        bus = Bus.Factory.builder().withEventLogger(logger).create();

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        bus.post(new Event());

        assertNotNull(logger.getEvent());
    }

}
