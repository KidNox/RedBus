package kidnox.eventbus.async;

import java.util.concurrent.*;

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
        this.threadFactory = factory;
        this.name = name;
        this.executor = executor;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        executor.setRejectedExecutionHandler(handler);
    }

    public Thread getWorkerThread() {
        return threadFactory.thread;
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
