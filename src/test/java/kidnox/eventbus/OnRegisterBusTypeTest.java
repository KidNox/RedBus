package kidnox.eventbus;

import kidnox.eventbus.test.RegUnregListeners;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OnRegisterBusTypeTest {

    @Parameterized.Parameters public static Collection getTargets() {
        return Arrays.asList(new Object[][]{
                {new SubscriberWithListeners()},
                {new ProducerWithListeners()},

        });
    }

    final Bus bus = Bus.Factory.createDefault();
    final RegUnregListeners target;

    public OnRegisterBusTypeTest(RegUnregListeners target) {
        this.target =  target;
    }

    @Test public void testUnreg() {
        bus.register(target);
        assertNotNull(target.getBus());
        assertEquals(target.getBus(), bus);
    }

    @Subscriber
    static class SubscriberWithListeners extends RegUnregListeners {
        @OnRegister public void onRegister(Bus bus) {
            setBus(bus);
        }
    }

    @Producer
    static class ProducerWithListeners extends RegUnregListeners {
        @OnRegister public void onRegister(Bus bus) {
            setBus(bus);
        }

    }


}
