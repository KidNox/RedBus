package kidnox.eventbus.async;

import kidnox.eventbus.EventDispatcher;

import static kidnox.eventbus.internal.Utils.checkNotNull;

public final class AsyncEventDispatcherExt implements EventDispatcher {

    public final Worker worker;

    public AsyncEventDispatcherExt(Worker worker) {
        this.worker = checkNotNull(worker);
    }

    @Override public void dispatch(Runnable runnable) {
        worker.execute(runnable);
    }

    @Override public boolean isDispatcherThread() {
        return worker.inWorkerThread();
    }

}
