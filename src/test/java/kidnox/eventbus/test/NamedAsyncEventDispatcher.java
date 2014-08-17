package kidnox.eventbus.test;

import kidnox.eventbus.async.SingleThreadWorker;
import kidnox.eventbus.impl.AsyncEventDispatcher;

public class NamedAsyncEventDispatcher extends AsyncEventDispatcher {

    final String name;
    final SingleThreadWorker worker;

    public NamedAsyncEventDispatcher(String name) {
        this(name, new SingleThreadWorker("worker-"+name));
    }

    public NamedAsyncEventDispatcher(String name, SingleThreadWorker worker) {
        this.name = name;
        this.worker = worker;
    }

    public String getName() {
        return name;
    }

    @Override protected void dispatch(Runnable runnable) {
        worker.execute(runnable);
    }

    @Override public boolean isDispatcherThread() {
        return worker.inWorkerThread();
    }

    public Thread getThread() {
        return worker.getWorkerThread();
    }

    public SingleThreadWorker getWorker() {
        return worker;
    }
}