package kidnox.eventbus;

import org.junit.Before;
import org.junit.Test;

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


}
