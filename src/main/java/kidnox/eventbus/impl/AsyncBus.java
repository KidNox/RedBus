package kidnox.eventbus.impl;

import kidnox.annotations.Beta;
import kidnox.eventbus.AnnotationFinder;
import kidnox.eventbus.DeadEventHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Beta
public class AsyncBus extends BusImpl {

    final Executor busExecutor = Executors.newSingleThreadExecutor();

    public AsyncBus(String name, AnnotationFinder annotationFinder, DeadEventHandler deadEventHandler) {
        super(name, annotationFinder, deadEventHandler);
    }

    @Override
    public void register(final Object target) {
        execute(new Runnable() {
            @Override
            public void run() {
                AsyncBus.super.register(target);
            }
        });
    }

    @Override
    public void unregister(final Object target) {
        execute(new Runnable() {
            @Override
            public void run() {
                AsyncBus.super.unregister(target);
            }
        });
    }

    @Override
    public void post(final Object event) {
        execute(new Runnable() {
            @Override
            public void run() {
                AsyncBus.super.post(event);
            }
        });
    }

    private void execute(Runnable runnable) {
        busExecutor.execute(runnable);
    }

}
