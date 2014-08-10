package kidnox.eventbus;

import kidnox.eventbus.internal.*;
import org.junit.Before;
import org.junit.Test;

import static kidnox.eventbus.impl.PackageLocalProvider.getProducersCache;
import static kidnox.eventbus.impl.PackageLocalProvider.getSubscibersCache;
import static org.junit.Assert.assertEquals;

public class InheritanceTest {

    ClassInfoExtractor classInfoExtractor;

    @Before public void setUp() {
        classInfoExtractor = InternalFactory.createClassInfoExtractor(null, false);
    }

    @Test public void overriddenSubscriberTest() {
        @Subscriber
        class SubscriberExt extends SimpleSubscriber {
            @Override @Subscribe public void obtainEvent(Event event) {}
        }

        classInfoExtractor.getTypeOf(SubscriberExt.class);
        assertEquals(getSubscibersCache(classInfoExtractor).size(), 1);
    }

    @Test public void overriddenProducerTest() {
        @Producer
        class ProducerExt extends SimpleProducer {
            @Override @Produce public Event produceEvent() {
                return super.produceEvent();
            }
        }
        classInfoExtractor.getTypeOf(ProducerExt.class);
        assertEquals(getProducersCache(classInfoExtractor).size(), 1);
    }

    @Test public void subscriberInheritanceTest() {
        @Subscriber
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainString(String s) {}
        }

        classInfoExtractor.getTypeOf(Subscriber1.class);
        assertEquals(getSubscibersCache(classInfoExtractor).get(Subscriber1.class).size(), 2);
    }

    @Test public void subscriberInheritanceTest2() {
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainString(String s) {}
        }

        @Subscriber
        class Subscriber2 extends Subscriber1 {
            @Override @Subscribe public void obtainString(String s) {}
        }

        classInfoExtractor.getTypeOf(Subscriber2.class);
        assertEquals(getSubscibersCache(classInfoExtractor).get(Subscriber2.class).size(), 1);
    }

    @Test public void subscribeInheritanceTest3() {
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainString(String s) {}
        }

        @Subscriber
        class Subscriber2 extends Subscriber1 {
            @Override @Subscribe public void obtainEvent(Event event) {}
        }

        classInfoExtractor.getTypeOf(Subscriber2.class);
        assertEquals(getSubscibersCache(classInfoExtractor).get(Subscriber2.class).size(), 1);
    }

    @Test public void producerInheritanceTest() {
        @Producer
        class Producer1 extends SimpleProducer {
            @Produce public String produceString() {
                return "";
            }
        }

        classInfoExtractor.getTypeOf(Producer1.class);
        assertEquals(getProducersCache(classInfoExtractor).get(Producer1.class).size(), 2);
    }

    @Test public void producerInheritanceTest2() {
        class Producer1 extends SimpleProducer {
            @Produce public String produceString() {
                return "";
            }
        }

        @Producer
        class Producer2 extends Producer1 {
            @Override @Produce public String produceString() {
                return super.produceString();
            }
        }

        classInfoExtractor.getTypeOf(Producer2.class);
        assertEquals(getProducersCache(classInfoExtractor).get(Producer2.class).size(), 1);
    }

    @Test public void producerInheritanceTest3() {
        class Producer1 extends SimpleProducer {
            @Produce public String produceString() {
                return "";
            }
        }

        @Producer
        class Producer2 extends Producer1 {
            @Produce public Event produceEvent() {
                return new Event();
            }
        }

        classInfoExtractor.getTypeOf(Producer2.class);
        assertEquals(getProducersCache(classInfoExtractor).get(Producer2.class).size(), 1);
    }

}
