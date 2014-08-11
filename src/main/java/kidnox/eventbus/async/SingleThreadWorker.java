package kidnox.eventbus.async;

import java.util.concurrent.*;

import static kidnox.eventbus.util.Utils.checkNotNull;

public final class SingleThreadWorker implements Worker {

    final SingleThreadFactory threadFactory;

    protected final String name;
    protected final ThreadPoolExecutor executor;

    public SingleThreadWorker() {
        this("worker-thread");
    }

    public SingleThreadWorker(String name) {
        this(name, true, new LinkedBlockingQueue<Runnable>());
    }

    public SingleThreadWorker(String name, boolean daemon, BlockingQueue<Runnable> blockingQueue) {
        this(name, new SingleThreadFactory(name, daemon), blockingQueue);
    }

    protected SingleThreadWorker(String name, SingleThreadFactory factory, BlockingQueue<Runnable> blockingQueue) {
        this(name, factory, new ThreadPoolExecutor(1, 1, 0, TimeUnit.NANOSECONDS, blockingQueue, factory));
    }

    protected SingleThreadWorker(String name, SingleThreadFactory factory, ThreadPoolExecutor executor) {
        this.threadFactory = checkNotNull(factory);
        this.name = checkNotNull(name);
        this.executor = checkNotNull(executor);
    }

    public SingleThreadWorker withRejectedExecutionHandler(RejectedExecutionHandler handler) {
        executor.setRejectedExecutionHandler(handler);
        return this;
    }

    public SingleThreadWorker withUncaughtExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        if(exceptionHandler != null)
            threadFactory.setUncaughtExceptionHandler(exceptionHandler);
        return this;
    }

    public Thread getWorkerThread() {
        return threadFactory.thread;
    }

    public int getTaskCount() {
        return executor.getQueue().size();
    }

    @Override public boolean inWorkerThread() {
        return Thread.currentThread() == threadFactory.thread;
    }

    @Override public String getName() {
        return name;
    }

    @SuppressWarnings("NullableProblems")
    @Override public void execute(Runnable command) {
        executor.execute(command);
    }

    @Override public void dismiss(boolean mustFinishTasks) {
        if(mustFinishTasks) {
            executor.shutdown();
        } else {
            executor.shutdownNow();
        }
    }

}
