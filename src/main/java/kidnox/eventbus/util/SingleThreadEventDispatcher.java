package kidnox.eventbus.util;

import kidnox.eventbus.Dispatcher;

import java.util.concurrent.*;

public class SingleThreadEventDispatcher extends ThreadPoolExecutor implements Dispatcher {

    final SingleThreadFactory threadFactory;

    public SingleThreadEventDispatcher() {
        this(new LinkedBlockingQueue<Runnable>(), new SingleThreadFactory());
    }

    public SingleThreadEventDispatcher(BlockingQueue<Runnable> workQueue, SingleThreadFactory threadFactory) {
        super(1, 1, 0, TimeUnit.NANOSECONDS, workQueue, threadFactory);
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
