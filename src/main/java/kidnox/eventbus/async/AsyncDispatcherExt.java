package kidnox.eventbus.async;

import kidnox.eventbus.impl.AsyncDispatcher;

public final class AsyncDispatcherExt extends AsyncDispatcher {

    final Worker worker;

    public AsyncDispatcherExt(Worker worker) {
        this.worker = worker;
    }

    @Override protected void dispatch(Runnable runnable) {
        worker.execute(runnable);
    }

    @Override protected boolean inCurrentThread() {
        return worker.inWorkerThread();
    }
}
