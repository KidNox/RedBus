package kidnox.eventbus.impl;

import kidnox.eventbus.*;
import kidnox.eventbus.elements.AsyncEventSubscriber;
import kidnox.eventbus.elements.EventSubscriber;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**beta*/
public class AsyncBus extends BusImpl {

    public final Executor busExecutor;

    public AsyncBus(String name, ClassInfoExtractor classInfoExtractor, EventLogger logger,
                    DeadEventHandler deadEventHandler, Interceptor interceptor) {
        super(name, classInfoExtractor, logger, deadEventHandler, interceptor);
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

    @Override EventSubscriber getEventSubscriber(Class event, Object target, Method method, Dispatcher dispatcher) {
        return new AsyncEventSubscriber(event, target, method, dispatcher);
    }

    @Override public String toString() {
        return "Async"+super.toString();
    }
}
