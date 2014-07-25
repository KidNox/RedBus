package kidnox.eventbus;

import kidnox.common.Factory;
import kidnox.eventbus.impl.EventSubscriber;
import kidnox.eventbus.impl.PackageLocalProvider;
import kidnox.eventbus.internal.*;
import kidnox.utils.Collections;
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

    @Test public void classFilterTest() {
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

    @Test public void dispatcherFactoryTest() {
        bus = BusFactory.builder().withDispatcherFactory(new Factory<Dispatcher, String>() {
            @Override public Dispatcher get(String dispatcherName) {
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
        bus = builder.withDispatcherFactory(new Factory<Dispatcher, String>() {
            @Override public Dispatcher get(String dispatcherName) {
                return dispatcher;
            }
        }).create();

        ClassInfoExtractor classInfoExtractor = builder.classInfoExtractor;

        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);

        List<EventSubscriber> eventSubscribers = PackageLocalProvider.getSubscribers(subscriber, classInfoExtractor);
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

        SimpleSubscriber simpleSubscriber = new SimpleSubscriber();
        bus.register(simpleSubscriber);

        bus.post(new Object());

        assertNotNull(logger.getEvent());
        assertTrue(Collections.notEmpty(logger.getElementSet()));
    }

}
