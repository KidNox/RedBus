package kidnox.eventbus;

import kidnox.eventbus.test.RegUnregListeners;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class RegUnregListenersTest {

    @Parameterized.Parameters public static Collection getTargets() {
        return Arrays.asList(new Object[][]{
                {new SubscriberWithListeners()},
                {new ProducerWithListeners()},
                {new SubscriberWithListeners2()},
                {new ProducerWithListeners2()},
        });
    }

    final Bus bus = Bus.Factory.createDefault();
    final RegUnregListeners target;

    public RegUnregListenersTest(RegUnregListeners target) {
        this.target =  target;
    }

    @Test public void testListeners() {
        bus.register(target);
        assertEquals(1, target.getOnRegisterCallCount());
        assertEquals(0, target.getOnUnregisterCallCount());
        bus.unregister(target);
        assertEquals(1, target.getOnUnregisterCallCount());
    }

    @Subscriber
    static class SubscriberWithListeners extends RegUnregListeners {
        @OnRegister public void onRegister() {
            super.onRegister();
        }

        @OnUnregister public void onUnregister() {
            super.onUnregister();
        }
    }

    @Producer
    static class ProducerWithListeners extends RegUnregListeners {
        @OnRegister public void onRegister() {
            super.onRegister();
        }

        @OnUnregister public void onUnregister() {
            super.onUnregister();
        }
    }

    @Subscriber
    static class SubscriberWithListeners2 extends RegUnregListeners {
        @OnRegister public void onRegister(Bus bus) {
            super.onRegister();
            assertNotNull(bus);
        }

        @OnUnregister public void onUnregister(Bus bus) {
            super.onUnregister();
            assertNotNull(bus);
        }
    }

    @Producer
    static class ProducerWithListeners2 extends RegUnregListeners {
        @OnRegister public void onRegister(Bus bus) {
            super.onRegister();
            assertNotNull(bus);
        }

        @OnUnregister public void onUnregister(Bus bus) {
            super.onUnregister();
            assertNotNull(bus);
        }
    }

}
