package kidnox.eventbus.impl;

import kidnox.annotations.Beta;
import kidnox.eventbus.Bus;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Beta
public class AsyncBusDelegate implements Bus {

    final Executor busExecutor;
    final Bus bus;

    public AsyncBusDelegate(Bus bus) {
        busExecutor = Executors.newSingleThreadExecutor();
        this.bus = bus;
    }

    @Override
    public void register(final Object target) {
        execute(new Runnable() {
            @Override
            public void run() {
                bus.register(target);
            }
        });
    }

    @Override
    public void unregister(final Object target) {
        execute(new Runnable() {
            @Override
            public void run() {
                bus.unregister(target);
            }
        });
    }

    @Override
    public void post(final Object event) {
        execute(new Runnable() {
            @Override
            public void run() {
                bus.post(event);
            }
        });
    }

    private void execute(Runnable runnable) {
        busExecutor.execute(runnable);
    }

    @Override
    public String toString() {
        return bus.toString();
    }
}
