package kidnox.eventbus;

import kidnox.eventbus.internal.BusException;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.internal.extraction.ClassInfoExtractor;
import kidnox.eventbus.internal.ClassType;
import kidnox.eventbus.test.bad.BadChildProducer;
import kidnox.eventbus.test.bad.BadChildSubscriber;
import kidnox.eventbus.test.bad.BadProducer;
import kidnox.eventbus.test.bad.BadSubscriber;
import kidnox.eventbus.test.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static kidnox.eventbus.internal.extraction.PackageLocalProvider.getClassToInfoMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ClassExtractorTest {

    ClassInfoExtractor classInfoExtractor;

    ClassInfoExtractor createExtractor() {
        return InternalFactory.createClassInfoExtractor();
    }

    @Before public void setUp() {
        classInfoExtractor = createExtractor();
    }

    @Test public void classTypeTest() {
        ClassInfo classInfo = classInfoExtractor.getClassInfo(SimpleSubscriber.class);
        assertEquals(classInfo.type, ClassType.SUBSCRIBER);

        classInfo = classInfoExtractor.getClassInfo(SimpleProducer.class);
        assertEquals(classInfo.type, ClassType.PRODUCER);

        classInfo = classInfoExtractor.getClassInfo(SimpleNone.class);
        assertEquals(classInfo.type, ClassType.NONE);
    }

    @Test public void classInfoCacheTest() {
        classInfoExtractor.getClassInfo(SimpleSubscriber.class);
        classInfoExtractor.getClassInfo(SimpleProducer.class);
        classInfoExtractor.getClassInfo(SimpleEventServices.class);
        classInfoExtractor.getClassInfo(SimpleNone.class);

        assertEquals(getClassToInfoMap(classInfoExtractor).get(SimpleSubscriber.class).type, ClassType.SUBSCRIBER);
        assertEquals(getClassToInfoMap(classInfoExtractor).get(SimpleProducer.class).type, ClassType.PRODUCER);
        assertEquals(getClassToInfoMap(classInfoExtractor).get(SimpleEventServices.class).type, ClassType.SERVICE);
        assertEquals(getClassToInfoMap(classInfoExtractor).get(SimpleNone.class).type, ClassType.NONE);
    }

    @Test public void noneTest() {
        ClassInfo classType = classInfoExtractor.getClassInfo(BadSubscriber.class);
        assertEquals(classType.type, ClassType.NONE);
        classType = classInfoExtractor.getClassInfo(BadProducer.class);
        assertEquals(classType.type, ClassType.NONE);
        classType = classInfoExtractor.getClassInfo(BadChildSubscriber.class);
        assertEquals(classType.type, ClassType.NONE);
        classType = classInfoExtractor.getClassInfo(BadChildProducer.class);
        assertEquals(classType.type, ClassType.NONE);

        assertEquals(4, getClassToInfoMap(classInfoExtractor).size());
    }

    @Test public void methodsCountTest() {
        assertEquals(4, classInfoExtractor.getClassInfo(LargeSubscriber.class).elements.size());
        assertEquals(4, classInfoExtractor.getClassInfo(LargeProducer.class).elements.size());
        assertEquals(4, classInfoExtractor.getClassInfo(LargeProcessor.class).elements.size());
        assertEquals(6, classInfoExtractor.getClassInfo(LargeEventService.class).elements.size());
    }
    //for java 8
    @Test public void bridgeMethodIgnoreTest() {
        assertEquals(1, classInfoExtractor.getClassInfo(GenericSubscriberImpl.class).elements.size());
    }

    @Subscriber
    static class GenericSubscriberImpl implements GenericSubscriber<String> {
        @Subscribe @Override public void obtain(String event) { }
    }

    @Test public void sameSubscribeMethodTest() {
        @Subscriber
        class Subscriber1 {
            @Subscribe public void obtainEvent1(Event event) {}

            @Subscribe public void obtainEvent2(Event event) {}
        }

        try {
            classInfoExtractor.getClassInfo(Subscriber1.class);
            fail("to many subscribers for event");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test(expected = BusException.class)
    public void sameExtSubscriberTest() {
        @Subscriber
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainEvent2(Event event) {}
        }
        classInfoExtractor.getClassInfo(Subscriber1.class);
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
            classInfoExtractor.getClassInfo(Producer1.class);
            fail("to many producers for event");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test(expected = BusException.class)
    public void sameProducerExtTest() {
        @Producer
        class Producer1 extends SimpleProducer {
            @Produce public Event produceEvent2() {
                return new Event();
            }
        }
        classInfoExtractor.getClassInfo(Producer1.class);
    }


//    @Test public void getDispatcherTest() {
//        //default factory
//        assertEquals(getDispatcher("", classInfoExtractor), InternalFactory.CURRENT_THREAD_DISPATCHER);
//        try {
//            getDispatcher("not-registered-dispatcher", classInfoExtractor);
//            fail();
//        } catch (RuntimeException ignored) {
//            //ignored.printStackTrace();
//        }
//        //AsyncDispatcherFactory
//        final String name = "test-dispatcher";
//        classInfoExtractor = createExtractor(new AsyncDispatcherFactory().registerDispatcherForName(name));
//        assertTrue(getDispatcher(name, classInfoExtractor) instanceof AsyncEventDispatcherExt);
//        try {
//            getDispatcher("not-registered-dispatcher", classInfoExtractor);
//            fail();
//        } catch (RuntimeException ignored) {
//            //ignored.printStackTrace();
//        }
//    }
//
//    @Test public void dispatcherCacheTest() {
//        classInfoExtractor = createExtractor(
//                new AsyncDispatcherFactory().registerDispatchersForNames("test-1", "test-2", "test-3"));
//        getDispatcher("test-1", classInfoExtractor);
//        getDispatcher("test-2", classInfoExtractor);
//        getDispatcher("test-3", classInfoExtractor);
//        assertEquals(3, getDispatchersMap(classInfoExtractor).size());
//
//        getDispatcher("", classInfoExtractor);
//        assertEquals(4, getDispatchersMap(classInfoExtractor).size());
//    }

}
