package kidnox.eventbus;

import kidnox.eventbus.annotations.Subscribe;
import kidnox.eventbus.annotations.Subscriber;
import kidnox.eventbus.internal.AbsAsyncSubscriber;
import kidnox.eventbus.internal.AsyncDispatchersFactory;
import kidnox.eventbus.internal.NamedAsyncDispatcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class AsyncTest {

    Bus bus;

    @Test public void baseAsyncTest() throws InterruptedException {
        AsyncDispatchersFactory factory = new AsyncDispatchersFactory();
        bus = BusFactory.builder().withDispatcherFactory(factory).create();

        final NamedAsyncDispatcher dispatcher1 = new NamedAsyncDispatcher("worker-1");
        final NamedAsyncDispatcher dispatcher2 = new NamedAsyncDispatcher("worker-2");
        final NamedAsyncDispatcher dispatcher3 = new NamedAsyncDispatcher("worker-3");

        factory.addDispatcher(dispatcher1);
        factory.addDispatcher(dispatcher2);
        factory.addDispatcher(dispatcher3);

        @Subscriber("worker-1")
        class Worker1 extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                currentEvent = event;
                checkThread(dispatcher1.getThread());

                dispatcher1.getWorker().dismiss(true);
            }
        }

        @Subscriber("worker-2")
        class Worker2 extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                currentEvent = event;
                checkThread(dispatcher2.getThread());

                dispatcher2.getWorker().dismiss(true);
            }
        }

        @Subscriber("worker-3")
        class Worker3 extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                currentEvent = event;
                checkThread(dispatcher3.getThread());

                dispatcher3.getWorker().dismiss(true);
            }
        }

        AbsAsyncSubscriber subscriber1 = new Worker1();
        AbsAsyncSubscriber subscriber2 = new Worker2();
        AbsAsyncSubscriber subscriber3 = new Worker3();

        bus.register(subscriber1);
        bus.register(subscriber2);
        bus.register(subscriber3);

        Object event = new Object();
        bus.post(event);

        dispatcher1.getThread().join();
        dispatcher2.getThread().join();
        dispatcher3.getThread().join();

        assertEquals("wrong event", event, subscriber1.getCurrentEvent());
        assertEquals("wrong event", event, subscriber2.getCurrentEvent());
        assertEquals("wrong event", event, subscriber3.getCurrentEvent());
    }

    private void checkThread(Thread expected) {
        assertEquals("wrong thread", expected, Thread.currentThread());
    }

}
