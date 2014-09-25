package kidnox.eventbus;

import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.Event2;
import kidnox.eventbus.test.MutableEventHandler;
import kidnox.eventbus.test.MutableProducer;
import kidnox.eventbus.test.simple.SimpleSubscriber;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NullsTest {

    Bus bus;

    @Before public void setUp() {
        bus = Bus.Factory.createDefault();
    }

    @Test(expected = NullPointerException.class)
    public void nullPost() {
        bus.post(null);
    }

    @Test (expected = NullPointerException.class)
    public void nullRegister() {
        bus.register(null);
    }

    @Test (expected = NullPointerException.class)
    public void nullUnregister() {
        bus.unregister(null);
    }

    //ignore null events from producers
    @Test public void nullProduce() {
        MutableProducer mutableProducer = new MutableProducer();
        SimpleSubscriber simpleSubscriber = new SimpleSubscriber();
        mutableProducer.setEvent(null);
        bus.register(simpleSubscriber);
        bus.register(mutableProducer);

        assertEquals(1, mutableProducer.getProducedCount());
        assertEquals(0, simpleSubscriber.getSubscribedCount());
    }

    //ignore null events from handler
    @Test public void nullHandle() {
        MutableEventHandler mutableEventHandler = new MutableEventHandler();
        SimpleSubscriber simpleSubscriber = new SimpleSubscriber();
        mutableEventHandler.setReturnValue(null);
        bus.register(mutableEventHandler);
        bus.register(simpleSubscriber);
        bus.post(new Event2());
        assertEquals(0, simpleSubscriber.getSubscribedCount());
        assertEquals(1, mutableEventHandler.getHandleCount());
    }

    //ignore null events from execute
    @Test public void nullExecute() {
        @Task class TestTask {
            int executeCalledCount;
            @Execute public Event execute() {
                executeCalledCount++;
                return null;
            }
        }
        SimpleSubscriber simpleSubscriber = new SimpleSubscriber();
        TestTask testTask = new TestTask();
        bus.register(simpleSubscriber);
        bus.register(testTask);
        assertNull(simpleSubscriber.getCurrentEvent());
        assertEquals(1, testTask.executeCalledCount);
    }

}
