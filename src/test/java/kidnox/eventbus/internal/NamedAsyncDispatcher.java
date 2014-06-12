package kidnox.eventbus.internal;

import kidnox.async.impl.WorkerImpl;
import kidnox.eventbus.impl.AsyncDispatcher;

public class NamedAsyncDispatcher extends AsyncDispatcher {

    final String name;
    final WorkerImpl worker;

    public NamedAsyncDispatcher(String name) {
        this(name, new WorkerImpl("worker-"+name));
    }

    public NamedAsyncDispatcher(String name, WorkerImpl worker) {
        this.name = name;
        this.worker = worker;
    }

    public String getName() {
        return name;
    }

    @Override
    protected void dispatch(Runnable runnable) {
        worker.execute(runnable);
    }

    @Override
    protected boolean inCurrentThread() {
        return worker.inWorkerThread();
    }

    public Thread getThread() {
        return worker.getWorkerThread();
    }

    public WorkerImpl getWorker() {
        return worker;
    }
}
