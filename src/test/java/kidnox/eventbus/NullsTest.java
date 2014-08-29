package kidnox.eventbus;

import kidnox.eventbus.test.MutableProducer;
import kidnox.eventbus.test.SimpleSubscriber;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

}
