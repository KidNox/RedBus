package kidnox.eventbus;

import kidnox.eventbus.internal.BusException;
import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.simple.SimpleSubscriber;
import kidnox.eventbus.test.simple.SimpleTask;
import kidnox.eventbus.test.simple.SimpleTask2;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

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
        SimpleTask2 simpleTask2 = new SimpleTask2();
        bus.register(simpleTask2);
        try {
            bus.unregister(simpleTask2);
            fail();
        } catch (BusException ignored) {}
    }

    @Test public void stubTaskTest() {
        @Task
        class StubTask {}
        StubTask task = new StubTask();
        bus.register(task);
        try {
            bus.unregister(task);
            fail();
        } catch (BusException ignored) {}
    }

    @Test public void executeReturnValueTest() {
        @Task class TestTask {
            @Execute public Event execute() {
                return new Event();
            }
        }
        TestTask testTask = new TestTask();
        SimpleSubscriber subscriber = new SimpleSubscriber();
        bus.register(subscriber);
        assertNull(subscriber.getCurrentEvent());
        bus.register(testTask);
        assertNotNull(subscriber.getCurrentEvent());
    }

}
