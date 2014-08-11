package kidnox.eventbus;

import kidnox.eventbus.async.AsyncEventDispatcherExt;
import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.elements.ClassProducers;
import kidnox.eventbus.elements.ClassSubscribers;
import kidnox.eventbus.test.ClassType;
import kidnox.eventbus.test.InternalFactory;
import kidnox.eventbus.test.bad.BadChildProducer;
import kidnox.eventbus.test.bad.BadChildSubscriber;
import kidnox.eventbus.test.bad.BadProducer;
import kidnox.eventbus.test.bad.BadSubscriber;
import kidnox.eventbus.test.*;
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
public class ClassExtractorTest {

    @SuppressWarnings("unchecked") @Parameterized.Parameters
    public static Collection classExtractorFactories() {
        return Arrays.asList(new Object[][] {{Factory.CLASS_INFO_EXTRACTOR_FACTORY},
                        {Factory.CLASS_INFO_EXTRACTOR_VALIDATION_FACTORY}});
    }

    final Factory<ClassInfoExtractor, EventDispatcher.Factory> extractorFactory;
    ClassInfoExtractor classInfoExtractor;

    public ClassExtractorTest(Factory<ClassInfoExtractor, EventDispatcher.Factory> extractorFactory) {
        this.extractorFactory = extractorFactory;
    }

    ClassInfoExtractor createExtractor(EventDispatcher.Factory factory) {
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

        assertEquals(4, getClassToTypeMap(classInfoExtractor).size());
        assertEquals(0, getSubscibersCache(classInfoExtractor).size());
        assertEquals(0, getProducersCache(classInfoExtractor).size());
    }

    @Test public void subscribersCacheTest() {
        classInfoExtractor.getTypeOf(SimpleSubscriber.class);
        ClassSubscribers classSubscribers = getSubscibersCache(classInfoExtractor).get(SimpleSubscriber.class);
        assertNotNull(classSubscribers);
        assertNotNull(classSubscribers.eventDispatcher);
        assertNotNull(classSubscribers.typedMethodsMap);
        assertFalse(classSubscribers.typedMethodsMap.isEmpty());
        assertFalse(ClassSubscribers.isNullOrEmpty(classSubscribers));

        classInfoExtractor.getTypeOf(LargeSubscriber.class);
        classSubscribers = getSubscibersCache(classInfoExtractor).get(LargeSubscriber.class);
        assertEquals(4, classSubscribers.typedMethodsMap.size());
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
        assertEquals(4, classProducers.typedMethodsMap.size());
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

    @Test(expected = IllegalStateException.class)
    public void sameExtSubscriberTest() {
        @Subscriber
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainEvent2(Event event) {}
        }
        classInfoExtractor.getTypeOf(Subscriber1.class);
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

    @Test(expected = IllegalStateException.class)
    public void sameProducerExtTest() {
        @Producer
        class Producer1 extends SimpleProducer {
            @Produce public Event produceEvent2() {
                return new Event();
            }
        }
        classInfoExtractor.getTypeOf(Producer1.class);
    }

    @Test public void getDispatcherTest() {
        //default factory
        assertEquals(getDispatcher("", classInfoExtractor), InternalFactory.CURRENT_THREAD_DISPATCHER);
        try {
            getDispatcher("not-registered-dispatcher", classInfoExtractor);
            fail();
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
        //AsyncDispatcherFactory
        final String name = "test-dispatcher";
        classInfoExtractor = createExtractor(new AsyncDispatcherFactory().registerDispatcherForName(name));
        assertTrue(getDispatcher(name, classInfoExtractor) instanceof AsyncEventDispatcherExt);
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
        assertEquals(3, getDispatchersMap(classInfoExtractor).size());

        getDispatcher("", classInfoExtractor);
        assertEquals(4, getDispatchersMap(classInfoExtractor).size());
    }

}
