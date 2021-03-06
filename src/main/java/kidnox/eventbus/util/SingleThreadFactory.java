package kidnox.eventbus.util;

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
        thread = new Thread(runnableProxy = new RunnableProxy());
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


    static class RunnableProxy implements Runnable {
        volatile Runnable real;

        @Override public void run() {
            real.run();
        }
    }
}
