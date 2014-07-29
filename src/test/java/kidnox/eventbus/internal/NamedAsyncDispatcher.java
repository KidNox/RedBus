package kidnox.eventbus.internal;

import kidnox.eventbus.async.SingleThreadWorker;
import kidnox.eventbus.impl.AsyncDispatcher;

public class NamedAsyncDispatcher extends AsyncDispatcher {

    final String name;
    final SingleThreadWorker worker;

    public NamedAsyncDispatcher(String name) {
        this(name, new SingleThreadWorker("worker-"+name));
    }

    public NamedAsyncDispatcher(String name, SingleThreadWorker worker) {
        this.name = name;
        this.worker = worker;
    }

    public String getName() {
        return name;
    }

    @Override protected void dispatch(Runnable runnable) {
        worker.execute(runnable);
    }

    @Override protected boolean inCurrentThread() {
        return worker.inWorkerThread();
    }

    public Thread getThread() {
        return worker.getWorkerThread();
    }

    public SingleThreadWorker getWorker() {
        return worker;
    }
}