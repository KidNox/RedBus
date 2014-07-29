package kidnox.eventbus;

import kidnox.eventbus.async.AsyncDispatcherExt;
import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.internal.bad.BadChildProducer;
import kidnox.eventbus.internal.bad.BadChildSubscriber;
import kidnox.eventbus.internal.bad.BadProducer;
import kidnox.eventbus.internal.bad.BadSubscriber;
import kidnox.eventbus.impl.BusDefaults;
import kidnox.eventbus.internal.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static kidnox.eventbus.impl.PackageLocalProvider.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Test both ClassInfoExtractorImpl and ClassInfoExtractorValidation
 * */
@RunWith(Parameterized.class)
public class ClassInfoExtractorTest {

    @SuppressWarnings("unchecked") @Parameterized.Parameters
    public static Collection classExtractorFactories() {
        return Arrays.asList(new Object[][] {{Factory.CLASS_INFO_EXTRACTOR_FACTORY},
                        {Factory.CLASS_INFO_EXTRACTOR_VALIDATION_FACTORY}});
    }

    final Factory<ClassInfoExtractor, Dispatcher.Factory> extractorFactory;
    ClassInfoExtractor classInfoExtractor;

    public ClassInfoExtractorTest(Factory<ClassInfoExtractor, Dispatcher.Factory> extractorFactory) {
        this.extractorFactory = extractorFactory;
    }

    ClassInfoExtractor createExtractor(Dispatcher.Factory factory) {
        return extractorFactory.get(factory);
    }

    @Before public void setUp() {
        classInfoExtractor = createExtractor(null);
    }

    @Test public void classTypeTest() {
        ClassType classType = classInfoExtractor.getTypeOf(SimpleSubscriber.class);
        assertEquals(classType, ClassType.SUBSCRIBER);

        classType = classInfoExtractor.getTypeOf(SimpleProducer.class);
        assertEquals(classType, ClassType.PRODUCER);

        classType = classInfoExtractor.getTypeOf(SimpleNone.class);
        assertEquals(classType, ClassType.NONE);
    }

    @Test public void classTypeCacheTest() {
        classInfoExtractor.getTypeOf(SimpleSubscriber.class);
        classInfoExtractor.getTypeOf(SimpleProducer.class);
        classInfoExtractor.getTypeOf(SimpleNone.class);

        assertEquals(getClassToTypeMap(classInfoExtractor).get(SimpleSubscriber.class), ClassType.SUBSCRIBER);
        assertEquals(getClassToTypeMap(classInfoExtractor).get(SimpleProducer.class), ClassType.PRODUCER);
        assertEquals(getClassToTypeMap(classInfoExtractor).get(SimpleNone.class), ClassType.NONE);
    }

    @Test public void noneTest() {
        ClassType classType = classInfoExtractor.getTypeOf(BadSubscriber.class);
        assertEquals(classType, ClassType.NONE);
        classType = classInfoExtractor.getTypeOf(BadProducer.class);
        assertEquals(classType, ClassType.NONE);
        classType = classInfoExtractor.getTypeOf(BadChildSubscriber.class);
        assertEquals(classType, ClassType.NONE);
        classType = classInfoExtractor.getTypeOf(BadChildProducer.class);
        assertEquals(classType, ClassType.NONE);

        assertEquals(getClassToTypeMap(classInfoExtractor).size(), 4);
        assertEquals(getSubscibersCache(classInfoExtractor).size(), 0);
        assertEquals(getProducersCache(classInfoExtractor).size(), 0);
    }

    @Test public void subscribersCacheTest() {
        classInfoExtractor.getTypeOf(SimpleSubscriber.class);
        ClassSubscribers classSubscribers = getSubscibersCache(classInfoExtractor).get(SimpleSubscriber.class);
        assertNotNull(classSubscribers);
        assertNotNull(classSubscribers.dispatcher);
        assertNotNull(classSubscribers.typedMethodsMap);
        assertFalse(classSubscribers.typedMethodsMap.isEmpty());
        assertFalse(ClassSubscribers.isNullOrEmpty(classSubscribers));

        classInfoExtractor.getTypeOf(LargeSubscriber.class);
        classSubscribers = getSubscibersCache(classInfoExtractor).get(LargeSubscriber.class);
        assertEquals(classSubscribers.typedMethodsMap.size(), 4);
    }

    @Test public void producersCacheTest() {
        classInfoExtractor.getTypeOf(SimpleProducer.class);
        ClassProducers classProducers = getProducersCache(classInfoExtractor).get(SimpleProducer.class);
        assertNotNull(classProducers);
        assertNotNull(classProducers.typedMethodsMap);
        assertFalse(classProducers.typedMethodsMap.isEmpty());
        assertFalse(ClassProducers.isNullOrEmpty(classProducers));

        classInfoExtractor.getTypeOf(LargeProducer.class);
        classProducers = getProducersCache(classInfoExtractor).get(LargeProducer.class);
        assertEquals(classProducers.typedMethodsMap.size(), 4);
    }

    @Test public void sameSubscribeMethodTest() {
        @Subscriber
        class Subscriber1 {
            @Subscribe public void obtainEvent1(Event event) {}

            @Subscribe public void obtainEvent2(Event event) {}
        }

        try {
            classInfoExtractor.getTypeOf(Subscriber1.class);
            fail("to many subscribers for event");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test public void sameExtSubscriberTest() {
        @Subscriber
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainEvent2(Event event) {}
        }

        try {
            classInfoExtractor.getTypeOf(Subscriber1.class);
            fail("to many subscribers for event");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test public void sameProduceMethodTest() {
        @Producer
        class Producer1 {
            @Produce public Event produceEvent1() {
                return new Event();
            }

            @Produce public Event produceEvent2() {
                return new Event();
            }
        }

        try {
            classInfoExtractor.getTypeOf(Producer1.class);
            fail("to many producers for event");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test public void sameProducerExtTest() {
        @Producer
        class Producer1 extends SimpleProducer {
            @Produce public Event produceEvent2() {
                return new Event();
            }
        }

        try {
            classInfoExtractor.getTypeOf(Producer1.class);
            fail("to many producers for event");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test public void getDispatcherTest() {
        //default factory
        assertEquals(getDispatcher("", classInfoExtractor), BusDefaults.CURRENT_THREAD_DISPATCHER);
        try {
            getDispatcher("not-registered-dispatcher", classInfoExtractor);
            fail();
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
        //AsyncDispatcherFactory
        final String name = "test-dispatcher";
        classInfoExtractor = createExtractor(new AsyncDispatcherFactory().registerDispatcherForName(name));
        assertTrue(getDispatcher(name, classInfoExtractor) instanceof AsyncDispatcherExt);
        try {
            getDispatcher("not-registered-dispatcher", classInfoExtractor);
            fail();
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test public void dispatcherCacheTest() {
        classInfoExtractor = createExtractor(
                new AsyncDispatcherFactory().registerDispatchersForNames("test-1", "test-2", "test-3"));
        getDispatcher("test-1", classInfoExtractor);
        getDispatcher("test-2", classInfoExtractor);
        getDispatcher("test-3", classInfoExtractor);
        assertEquals(getDispatchersMap(classInfoExtractor).size(), 3);

        getDispatcher("", classInfoExtractor);
        assertEquals(getDispatchersMap(classInfoExtractor).size(), 4);
    }

}
