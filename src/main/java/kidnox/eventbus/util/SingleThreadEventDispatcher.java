package kidnox.eventbus.util;

import kidnox.eventbus.Dispatcher;

import java.util.concurrent.*;

public class SingleThreadEventDispatcher extends ThreadPoolExecutor implements Dispatcher {

    public final String name;
    final SingleThreadFactory threadFactory;

    public SingleThreadEventDispatcher(String name) {
        this(name, new LinkedBlockingQueue<Runnable>(), new SingleThreadFactory(name, false));
    }

    public SingleThreadEventDispatcher(String name, BlockingQueue<Runnable> workQueue,
                                       SingleThreadFactory threadFactory) {
        super(1, 1, 0, TimeUnit.NANOSECONDS, workQueue, threadFactory);
        this.name = name;
        this.threadFactory = threadFactory;
    }

    @Override public boolean isDispatcherThread() {
        return Thread.currentThread() == threadFactory.thread;
    }

    @Override public void dispatch(Runnable event) {
        execute(event);
    }

    public Thread getThread() {
        return threadFactory.thread;
    }

    public SingleThreadEventDispatcher withUncaughtExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        if(exceptionHandler != null)
            threadFactory.setUncaughtExceptionHandler(exceptionHandler);
        return this;
    }
}
