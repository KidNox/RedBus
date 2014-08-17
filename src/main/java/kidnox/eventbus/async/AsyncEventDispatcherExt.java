package kidnox.eventbus.async;

import kidnox.eventbus.impl.AsyncEventDispatcher;

import static kidnox.eventbus.internal.Utils.checkNotNull;

public final class AsyncEventDispatcherExt extends AsyncEventDispatcher {

    public final Worker worker;

    public AsyncEventDispatcherExt(Worker worker) {
        this.worker = checkNotNull(worker);
    }

    @Override protected void dispatch(Runnable runnable) {
        worker.execute(runnable);
    }

    @Override public boolean isDispatcherThread() {
        return worker.inWorkerThread();
    }

}
