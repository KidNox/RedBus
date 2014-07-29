package kidnox.eventbus.async;

import kidnox.eventbus.impl.AsyncDispatcher;

import static kidnox.eventbus.utils.Utils.checkNotNull;

public final class AsyncDispatcherExt extends AsyncDispatcher {

    public final Worker worker;

    public AsyncDispatcherExt(Worker worker) {
        this.worker = checkNotNull(worker);
    }

    @Override protected void dispatch(Runnable runnable) {
        worker.execute(runnable);
    }

    @Override protected boolean inCurrentThread() {
        return worker.inWorkerThread();
    }

}
