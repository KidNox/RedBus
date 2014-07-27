package kidnox.eventbus.impl;

import kidnox.eventbus.ClassInfoExtractor;
import kidnox.eventbus.DeadEventHandler;
import kidnox.eventbus.EventLogger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncBus extends BusImpl {

    final Executor busExecutor;

    public AsyncBus(String name, ClassInfoExtractor classInfoExtractor,
                    EventLogger logger, DeadEventHandler deadEventHandler) {
        super(name, classInfoExtractor, logger, deadEventHandler);
        busExecutor = Executors.newSingleThreadExecutor();
    }

    @Override public void register(final Object target) {
        execute(new Runnable() {
            @Override
            public void run() {
                AsyncBus.super.register(target);
            }
        });
    }

    @Override public void unregister(final Object target) {
        execute(new Runnable() {
            @Override
            public void run() {
                AsyncBus.super.unregister(target);
            }
        });
    }

    @Override public void post(final Object event) {
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

    @Override public String toString() {
        return "Async"+super.toString();
    }
}
