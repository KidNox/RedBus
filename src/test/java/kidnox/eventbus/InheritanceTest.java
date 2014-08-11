package kidnox.eventbus;

import kidnox.eventbus.elements.ClassProducers;
import kidnox.eventbus.elements.ClassSubscribers;
import kidnox.eventbus.test.*;
import org.junit.Before;
import org.junit.Test;

import static kidnox.eventbus.impl.PackageLocalProvider.getProducersCache;
import static kidnox.eventbus.impl.PackageLocalProvider.getSubscibersCache;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        assertEquals(1, getSubscibersCache(classInfoExtractor).size());
    }

    @Test public void overriddenProducerTest() {
        @Producer
        class ProducerExt extends SimpleProducer {
            @Override @Produce public Event produceEvent() {
                return super.produceEvent();
            }
        }
        classInfoExtractor.getTypeOf(ProducerExt.class);
        assertEquals(1, getProducersCache(classInfoExtractor).size());
    }

    @Test public void overriddenSubscriberTest2() {
        @Subscriber
        class SubscriberExt extends SimpleSubscriber {}

        classInfoExtractor.getTypeOf(SubscriberExt.class);
        assertEquals(1, getSubscibersCache(classInfoExtractor).size());
        assertFalse(ClassSubscribers.isNullOrEmpty(
                getSubscibersCache(classInfoExtractor).entrySet().iterator().next().getValue()));
    }

    @Test public void overriddenProducerTest2() {
        @Producer
        class ProducerExt extends SimpleProducer {}

        classInfoExtractor.getTypeOf(ProducerExt.class);
        assertEquals(1, getProducersCache(classInfoExtractor).size());
        assertFalse(ClassProducers.isNullOrEmpty(
                getProducersCache(classInfoExtractor).entrySet().iterator().next().getValue()));
    }

    @Test public void overriddenSubscriberTest3() {
        @Subscriber
        class SubscriberExt extends SimpleSubscriber {
            @Override public void obtainEvent(Event event) {
                super.obtainEvent(event);
            }
        }

        classInfoExtractor.getTypeOf(SubscriberExt.class);
        assertEquals(1, getSubscibersCache(classInfoExtractor).size());
    }

    @Test public void overriddenProducerTest3() {
        @Producer
        class ProducerExt extends SimpleProducer {
            @Override public Event produceEvent() {
                return super.produceEvent();
            }
        }

        classInfoExtractor.getTypeOf(ProducerExt.class);
        assertEquals(1, getProducersCache(classInfoExtractor).size());
    }

    @Test public void subscriberInheritanceTest() {
        @Subscriber
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainString(String s) {}
        }

        classInfoExtractor.getTypeOf(Subscriber1.class);
        assertEquals(2, getSubscibersCache(classInfoExtractor).get(Subscriber1.class).size());
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
        assertEquals(1, getSubscibersCache(classInfoExtractor).get(Subscriber2.class).size());
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
        assertEquals(1, getSubscibersCache(classInfoExtractor).get(Subscriber2.class).size());
    }

    @Test public void producerInheritanceTest() {
        @Producer
        class Producer1 extends SimpleProducer {
            @Produce public String produceString() {
                return "";
            }
        }

        classInfoExtractor.getTypeOf(Producer1.class);
        assertEquals(2, getProducersCache(classInfoExtractor).get(Producer1.class).size());
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
        assertEquals(1, getProducersCache(classInfoExtractor).get(Producer2.class).size());
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
        assertEquals(1, getProducersCache(classInfoExtractor).get(Producer2.class).size());
    }

}
