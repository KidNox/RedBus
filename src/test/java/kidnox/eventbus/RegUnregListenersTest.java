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
        assertTrue(target.isOnRegisterCall());
        assertFalse(target.isOnUnregisterCall());
        bus.unregister(target);
        assertTrue(target.isOnUnregisterCall());
    }

    @Subscriber
    static class SubscriberWithListeners extends RegUnregListeners {

        @OnRegister public void onRegister() {
            setOnRegisterCall(true);
        }

        @OnUnregister public void onUnregister() {
            setOnUnregisterCall(true);
        }

    }

    @Producer
    static class ProducerWithListeners extends RegUnregListeners {
        @OnRegister public void onRegister() {
            setOnRegisterCall(true);
        }

        @OnUnregister public void onUnregister() {
            setOnUnregisterCall(true);
        }
    }

    @Subscriber
    static class SubscriberWithListeners2 extends RegUnregListeners {
        @OnRegister public void onRegister(Bus bus) {
            setOnRegisterCall(true);
            assertNotNull(bus);
        }

        @OnUnregister public void onUnregister(Bus bus) {
            setOnUnregisterCall(true);
            assertNotNull(bus);
        }
    }

    @Producer
    static class ProducerWithListeners2 extends RegUnregListeners {
        @OnRegister public void onRegister(Bus bus) {
            setOnRegisterCall(true);
            assertNotNull(bus);
        }

        @OnUnregister public void onUnregister(Bus bus) {
            setOnUnregisterCall(true);
            assertNotNull(bus);
        }
    }

}
