package kidnox.eventbus;

import kidnox.eventbus.util.AsyncDispatcherFactory;
import kidnox.eventbus.test.*;
import kidnox.eventbus.util.SingleThreadEventDispatcher;
import org.junit.Test;

import java.util.concurrent.Semaphore;
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

    @Test (timeout = 1000)
    public void asyncPost() throws InterruptedException {
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
        final Dispatcher.Factory factory = new AsyncDispatcherFactory()
                .addDispatcher(Dispatcher.WORKER, AsyncDispatcherFactory.getWorkerDispatcher());
        final Bus bus = Bus.Factory.builder()
                .withEventDispatcherFactory(factory)
                .create();

        final SingleThreadEventDispatcher dispatcher = (SingleThreadEventDispatcher) factory.getDispatcher("worker");

        @Subscriber("worker")
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

    @Test (timeout = 1000)
    public void asyncSubscriberUnregisterTest() throws InterruptedException {
        final Dispatcher.Factory factory = new AsyncDispatcherFactory("worker");
        //here we catch event, that posted after async unregister
        SimpleDeadEventHandler deadEventHandler = new SimpleDeadEventHandler();
        final Bus bus = Bus.Factory.builder()
                .withEventDispatcherFactory(factory)
                .withDeadEventHandler(deadEventHandler)
                .create();

        final SingleThreadEventDispatcher dispatcher = (SingleThreadEventDispatcher) factory.getDispatcher("worker");

        final AtomicBoolean isRegistered = new AtomicBoolean();

        @Subscriber("worker")
        class SubscriberClass {
            @Subscribe public void obtainEvent(Object event) {
                fail("obtain event after unregister");
            }
        }

        final Semaphore semaphore = new Semaphore(0, true);

        final SubscriberClass subscriberClass = new SubscriberClass();
        bus.register(subscriberClass);
        isRegistered.set(true);//there are no async operations yet

        dispatcher.execute(new Runnable() {
            @Override public void run() {
                //release main thread and acquire dispatcher for posting
                //System.out.println("release main");
                semaphore.release();
                try {
                    //System.out.println("dispatcher lock");
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.out.println("unregister");
                bus.unregister(subscriberClass);
                isRegistered.set(false);
                dispatcher.shutdown();
            }
        });
        //System.out.println("main lock");
        semaphore.acquire();//wait until dispatcher run
        bus.post(new Object());
        //System.out.println("dispatcher unlock");
        //release dispatcher thread for unregister and wait for dispatcher shutdown
        semaphore.release();
        dispatcher.getThread().join();

        assertNotNull(deadEventHandler.getCurrentEvent());
        assertFalse(isRegistered.get());
    }

    @Test (timeout = 1000)
    public void produceTest() throws InterruptedException {
        final Thread thread = Thread.currentThread();

        final Dispatcher.Factory factory = new AsyncDispatcherFactory()
                .addDispatcher(Dispatcher.WORKER, AsyncDispatcherFactory.getWorkerDispatcher());
        final Bus bus = Bus.Factory.builder().withEventDispatcherFactory(factory).create();

        final SingleThreadEventDispatcher worker = TestUtils.getSTWorkerForName(Dispatcher.WORKER, factory);

        @Subscriber(Dispatcher.WORKER)
        class SubscriberClass extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Event event) {
                currentEvent = event;
                checkThread(worker.getThread());

                worker.shutdown();
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

        worker.getThread().join();

        assertEquals(1, producerClass.getProducedCount());
        assertNotNull(subscriberClass.getCurrentEvent());
    }

    @Test(timeout = 1000)
    public void asyncProduceTest() throws InterruptedException {
        final Dispatcher.Factory factory = new AsyncDispatcherFactory("subscriber", "producer");
        final Bus bus = Bus.Factory.builder().withEventDispatcherFactory(factory).create();

        final Semaphore semaphore = new Semaphore(0, true);

        final SingleThreadEventDispatcher subscriberDispatcher = TestUtils.getSTWorkerForName("subscriber", factory);
        final SingleThreadEventDispatcher producerDispatcher = TestUtils.getSTWorkerForName("producer", factory);

        @Subscriber("subscriber")
        class SubscriberClass extends AbsAsyncSubscriber {
            @Subscribe public void obtainEvent(Event event) {
                checkThread(subscriberDispatcher.getThread());
                currentEvent = event;
                semaphore.release();
            }
        }

        @Producer("producer")
        class ProducerClass extends AbsAsyncProducer {
            @Produce public Event produceEvent() {
                checkThread(producerDispatcher.getThread());
                producedCount++;
                Event event = new Event();
                lastEvent = event;
                return event;
            }
        }

        SubscriberClass subscriber = new SubscriberClass();
        ProducerClass producer = new ProducerClass();
        //subscriber first
        bus.register(subscriber);
        bus.register(producer);
        semaphore.acquire();

        assertEquals(1, producer.getProducedCount());
        assertEquals(subscriber.getCurrentEvent(), producer.getLastEvent());

        bus.unregister(subscriber);
        bus.unregister(producer);

        subscriber = new SubscriberClass();
        producer = new ProducerClass();
        //producer first
        bus.register(producer);
        bus.register(subscriber);
        semaphore.acquire();

        assertEquals(1, producer.getProducedCount());
        assertEquals(subscriber.getCurrentEvent(), producer.getLastEvent());

        subscriberDispatcher.shutdown();
        producerDispatcher.shutdown();
    }

    @Test (timeout = 1000)
    public void asyncProducerUnregisterTest() throws InterruptedException {
        final Dispatcher.Factory factory = new AsyncDispatcherFactory("worker");
        final Bus bus = Bus.Factory.builder().withEventDispatcherFactory(factory).create();

        final SingleThreadEventDispatcher dispatcher = (SingleThreadEventDispatcher) factory.getDispatcher("worker");
        final AtomicBoolean isRegistered = new AtomicBoolean();

        @Producer("worker")
        class ProducerClass {
            @Produce public Event produceEvent() {
                fail("produce event after unregister");
                return null;
            }
        }

        final Semaphore semaphore = new Semaphore(0, true);

        final ProducerClass producer = new ProducerClass();
        final SimpleSubscriber simpleSubscriber = new SimpleSubscriber();

        bus.register(producer);
        isRegistered.set(true);//there are no async operations yet

        dispatcher.execute(new Runnable() {
            @Override public void run() {
                //release main thread and acquire dispatcher for subscribing
                //System.out.println("release main");
                semaphore.release();
                try {
                    //System.out.println("dispatcher lock");
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.out.println("unregister");
                bus.unregister(producer);
                isRegistered.set(false);
                dispatcher.shutdown();
            }
        });
        //System.out.println("main lock");
        semaphore.acquire();//wait until dispatcher run
        bus.register(simpleSubscriber);
        //System.out.println("dispatcher unlock");
        //release dispatcher thread for unregister and wait for dispatcher shutdown
        semaphore.release();
        dispatcher.getThread().join();

        assertEquals(0, simpleSubscriber.getSubscribedCount());
        assertFalse(isRegistered.get());
    }

    @Test public void asyncRegisterTest() {
        @Subscriber class SubscriberWithListener {
            @OnRegister public void onrRegister() {

            }
        }
    }

    private void checkThread(Thread expected) {
        assertEquals("wrong thread", expected, Thread.currentThread());
    }

}
