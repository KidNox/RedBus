package kidnox.eventbus;

import kidnox.eventbus.util.AsyncDispatcherFactory;
import kidnox.eventbus.test.*;
import kidnox.eventbus.util.SingleThreadEventDispatcher;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static kidnox.eventbus.test.TestUtils.addDispatchersToFactory;
import static org.junit.Assert.*;

public class AsyncTest {

    Bus bus;

    @Test (timeout = 1000)
    public void baseAsyncTest() throws InterruptedException {
        AsyncDispatcherFactory factory = new AsyncDispatcherFactory();
        bus = Bus.Factory.builder().withEventDispatcherFactory(factory).create();

        final NamedAsyncEventDispatcher dispatcher1 = new NamedAsyncEventDispatcher("worker-1");
        final NamedAsyncEventDispatcher dispatcher2 = new NamedAsyncEventDispatcher("worker-2");
        final NamedAsyncEventDispatcher dispatcher3 = new NamedAsyncEventDispatcher("worker-3");

        addDispatchersToFactory(factory, dispatcher1, dispatcher2, dispatcher3);

        @Subscriber("worker-1")
        class Worker1 extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                currentEvent = event;
                checkThread(dispatcher1.getThread());

                dispatcher1.shutdown();
            }
        }

        @Subscriber("worker-2")
        class Worker2 extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                currentEvent = event;
                checkThread(dispatcher2.getThread());

                dispatcher2.shutdown();
            }
        }

        @Subscriber("worker-3")
        class Worker3 extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                currentEvent = event;
                checkThread(dispatcher3.getThread());

                dispatcher3.shutdown();
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

    @Test public void asyncPost() throws InterruptedException {
        final Bus bus = Bus.Factory.createDefault();
        final NamedAsyncEventDispatcher dispatcher1 = new NamedAsyncEventDispatcher("worker-1");

        @Subscriber
        class SubscriberClass extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                currentEvent = event;
                checkThread(dispatcher1.getThread());

                dispatcher1.shutdown();
            }
        }

        final SubscriberClass subscriberClass = new SubscriberClass();
        bus.register(subscriberClass);

        dispatcher1.execute(new Runnable() {
            @Override
            public void run() {
                bus.post(new Object());
            }
        });

        dispatcher1.getThread().join();

        assertNotNull("wrong event", subscriberClass.getCurrentEvent());
    }

    @Test public void testAsyncDispatcherFactory() throws InterruptedException {
        final EventDispatcher.Factory factory = new AsyncDispatcherFactory()
                .addDispatcher(EventDispatcher.WORKER, AsyncDispatcherFactory.getWorkerDispatcher());
        final Bus bus = Bus.Factory.builder()
                .withEventDispatcherFactory(factory)
                .create();

        final SingleThreadEventDispatcher dispatcher = (SingleThreadEventDispatcher) factory.getDispatcher(EventDispatcher.WORKER);

        @Subscriber(EventDispatcher.WORKER)
        class SubscriberClass extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                currentEvent = event;
                checkThread(dispatcher.getThread());
                dispatcher.shutdown();
            }
        }

        SubscriberClass subscriberClass = new SubscriberClass();
        bus.register(subscriberClass);
        bus.post(new Object());

        dispatcher.getThread().join();

        assertNotNull(subscriberClass.getCurrentEvent());
    }

    @Test public void asyncUnregisterTest() throws InterruptedException {
        final EventDispatcher.Factory factory = new AsyncDispatcherFactory()
                .addDispatcher(EventDispatcher.WORKER, AsyncDispatcherFactory.getWorkerDispatcher());
        final Bus bus = Bus.Factory.builder()
                .withEventDispatcherFactory(factory)
                .create();

        final SingleThreadEventDispatcher dispatcher = (SingleThreadEventDispatcher) factory.getDispatcher(EventDispatcher.WORKER);

        final AtomicBoolean isRegistered = new AtomicBoolean();
        final AtomicBoolean mustFail = new AtomicBoolean(false);

        @Subscriber(EventDispatcher.WORKER)
        class SubscriberClass extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Object event) {
                mustFail.set(!isRegistered.get());
            }
        }

        final SubscriberClass subscriberClass = new SubscriberClass();
        bus.register(subscriberClass);
        isRegistered.set(true);

        dispatcher.execute(new Runnable() {
            @Override public void run() {
                bus.unregister(subscriberClass);
                isRegistered.set(false);
                dispatcher.shutdown();
            }
        });

        bus.post(new Object());
        dispatcher.getThread().join();

        if(mustFail.get()) fail("obtain event after unregister");
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    @Test public void produceTest() throws InterruptedException {
        final Thread thread = Thread.currentThread();

        final EventDispatcher.Factory factory = new AsyncDispatcherFactory()
                .addDispatcher(EventDispatcher.WORKER, AsyncDispatcherFactory.getWorkerDispatcher());
        final Bus bus = Bus.Factory.builder()
                .withEventDispatcherFactory(factory)
                .create();

        @Subscriber(EventDispatcher.WORKER)
        class SubscriberClass extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Event event) {
                currentEvent = event;
                SingleThreadEventDispatcher worker = TestUtils.getSTWorkerForName(EventDispatcher.WORKER, factory);
                checkThread(worker.getThread());

                synchronized (thread) {
                    thread.notify();
                }
            }
        }

        @Producer
        class ProducerClass extends SimpleProducer {
            @Override @Produce public Event produceEvent() {
                checkThread(thread);
                return super.produceEvent();
            }
        }

        ProducerClass producerClass = new ProducerClass();
        SubscriberClass subscriberClass = new SubscriberClass();

        bus.register(producerClass);
        bus.register(subscriberClass);

        synchronized (thread) {
            thread.wait();
        }

        assertEquals(1, producerClass.getProducedCount());
        assertNotNull(subscriberClass.getCurrentEvent());
    }

    private void checkThread(Thread expected) {
        assertEquals("wrong thread", expected, Thread.currentThread());
    }

}
