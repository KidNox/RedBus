package kidnox.eventbus;

import kidnox.eventbus.internal.BusException;
import kidnox.eventbus.test.SimpleTask;
import kidnox.eventbus.test.SimpleTask2;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TaskLifecycleTest {

    Bus bus;

    @Before public void setUp() {
        bus = Bus.Factory.createDefault();
    }

    //test without listener methods in GeneralTest
    @Test public void testRegister() {
        SimpleTask2 simpleTask2 = new SimpleTask2();
        bus.register(simpleTask2);
        assertEquals(1, simpleTask2.getOnRegisterCount());
        assertEquals(1, simpleTask2.getExecuteCount());
        assertEquals(1, simpleTask2.getOnUnregisterCount());
    }

    @Test(expected = BusException.class)
    public void testUnregister() {
        bus.unregister(new SimpleTask());
    }

    @Test(expected = BusException.class)
    public void testUnregister2() {
        bus.unregister(new SimpleTask2());
    }

    @Test public void testUnregister3() {
        SimpleTask simpleTask = new SimpleTask();
        bus.register(simpleTask);
        try {
            bus.unregister(simpleTask);
            fail();
        } catch (BusException ignored) {}
    }

    @Test public void testUnregister4() {
        SimpleTask2 simpleTask = new SimpleTask2();
        bus.register(simpleTask);
        try {
            bus.unregister(simpleTask);
            fail();
        } catch (BusException ignored) {}
    }

}
