package kidnox.eventbus;

import kidnox.eventbus.test.MappedDispatchersFactory;
import org.junit.Before;

public class DispatcherTest {

    MappedDispatchersFactory dispatchersFactory;
    Bus bus;

    @Before public void setUp() {
        dispatchersFactory = new MappedDispatchersFactory();
        bus = Bus.Factory.builder().withEventDispatcherFactory(dispatchersFactory).build();
    }

}
