package kidnox.eventbus.async;

import java.util.concurrent.ThreadFactory;

public final class SingleThreadFactory implements ThreadFactory {

    final Thread thread;
    private final RunnableProxy runnableProxy;

    public SingleThreadFactory(String name, boolean daemon) {
        this();
        thread.setName(name);
        thread.setDaemon(daemon);
    }

    public SingleThreadFactory() {
        runnableProxy = new RunnableProxy();
        thread = new Thread(runnableProxy);
    }

    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        thread.setUncaughtExceptionHandler(handler);
    }

    @SuppressWarnings("NullableProblems")
    @Override public Thread newThread(Runnable r) {
        if(r == null) throw new NullPointerException();
        runnableProxy.real = r;
        return thread;
    }


    public static class RunnableProxy implements Runnable {
        volatile Runnable real;

        @Override public void run() {
            real.run();
        }
    }
}
