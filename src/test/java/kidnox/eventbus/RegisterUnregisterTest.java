package kidnox.eventbus;

import kidnox.eventbus.internal.Provider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class RegisterUnregisterTest {

    @SuppressWarnings("unchecked") @Parameterized.Parameters
    public static Collection classTypeInstancesProvider() {
        return Arrays.asList(new Object[][]{
                {Provider.SIMPLE_SUBSCRIBER_PROVIDER},
                {Provider.SIMPLE_PRODUCER_PROVIDER},
                {Provider.SIMPLE_NONE_PROVIDER}});
    }

    private Bus bus;

    final Object instance;

    public RegisterUnregisterTest(Provider provider) {
        this.instance = provider.get();
    }

    @Before public void setUp() throws Exception {
        bus = BusFactory.getDefault();
    }

    @Test public void registerUnregisterTest() {
        bus.register(instance);
        bus.unregister(instance);
    }

    @Test public void registerTwiceTest() {
        bus.register(instance);
        try {
            bus.register(instance);
            fail("already registered");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

    @Test public void unregisterWhenNotRegisteredTest() {
        try {
            bus.unregister(instance);
            fail("not registered");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }

}
