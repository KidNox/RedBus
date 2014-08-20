package kidnox.eventbus;

import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.test.*;
import org.junit.Before;
import org.junit.Test;

import static kidnox.eventbus.impl.PackageLocalProvider.getClassToInfoMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InheritanceTest {

    ClassInfoExtractor classInfoExtractor;

    @Before public void setUp() {
        classInfoExtractor = InternalFactory.createClassInfoExtractor(false);
    }

    @Test public void overriddenSubscriberTest() {
        @Subscriber
        class SubscriberExt extends SimpleSubscriber {
            @Override @Subscribe public void obtainEvent(Event event) {}
        }

        classInfoExtractor.getClassInfo(SubscriberExt.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).size());
    }

    @Test public void overriddenProducerTest() {
        @Producer
        class ProducerExt extends SimpleProducer {
            @Override @Produce public Event produceEvent() {
                return super.produceEvent();
            }
        }
        classInfoExtractor.getClassInfo(ProducerExt.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).size());
    }

    @Test public void overriddenSubscriberTest2() {
        @Subscriber
        class SubscriberExt extends SimpleSubscriber {}

        ClassInfo classInfo = classInfoExtractor.getClassInfo(SubscriberExt.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).size());
        assertFalse(classInfo.isEmpty());
    }

    @Test public void overriddenProducerTest2() {
        @Producer
        class ProducerExt extends SimpleProducer {}

        ClassInfo classInfo = classInfoExtractor.getClassInfo(ProducerExt.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).size());
        assertFalse(classInfo.isEmpty());
    }

    @Test public void overriddenSubscriberTest3() {
        @Subscriber
        class SubscriberExt extends SimpleSubscriber {
            @Override public void obtainEvent(Event event) {
                super.obtainEvent(event);
            }
        }

        classInfoExtractor.getClassInfo(SubscriberExt.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).size());
    }

    @Test public void overriddenProducerTest3() {
        @Producer
        class ProducerExt extends SimpleProducer {
            @Override public Event produceEvent() {
                return super.produceEvent();
            }
        }

        classInfoExtractor.getClassInfo(ProducerExt.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).size());
    }

    @Test public void subscriberInheritanceTest() {
        @Subscriber
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainString(String s) {}
        }

        classInfoExtractor.getClassInfo(Subscriber1.class);
        assertEquals(2, getClassToInfoMap(classInfoExtractor).get(Subscriber1.class).elements.size());
    }

    @Test public void subscriberInheritanceTest2() {
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainString(String s) {}
        }

        @Subscriber
        class Subscriber2 extends Subscriber1 {
            @Override @Subscribe public void obtainString(String s) {}
        }

        classInfoExtractor.getClassInfo(Subscriber2.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).get(Subscriber2.class).elements.size());
    }

    @Test public void subscribeInheritanceTest3() {
        class Subscriber1 extends SimpleSubscriber {
            @Subscribe public void obtainString(String s) {}
        }

        @Subscriber
        class Subscriber2 extends Subscriber1 {
            @Override @Subscribe public void obtainEvent(Event event) {}
        }

        classInfoExtractor.getClassInfo(Subscriber2.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).get(Subscriber2.class).elements.size());
    }

    @Test public void producerInheritanceTest() {
        @Producer
        class Producer1 extends SimpleProducer {
            @Produce public String produceString() {
                return "";
            }
        }

        classInfoExtractor.getClassInfo(Producer1.class);
        assertEquals(2, getClassToInfoMap(classInfoExtractor).get(Producer1.class).elements.size());
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

        classInfoExtractor.getClassInfo(Producer2.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).get(Producer2.class).elements.size());
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

        classInfoExtractor.getClassInfo(Producer2.class);
        assertEquals(1, getClassToInfoMap(classInfoExtractor).get(Producer2.class).elements.size());
    }

}
