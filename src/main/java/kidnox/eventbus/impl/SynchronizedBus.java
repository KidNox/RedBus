package kidnox.eventbus.impl;

import kidnox.eventbus.ExceptionHandler;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.DeadEventHandler;
import kidnox.eventbus.EventLogger;
import kidnox.eventbus.Interceptor;

public class SynchronizedBus extends BusImpl {

    public SynchronizedBus(String name, ClassInfoExtractor classInfoExtractor, EventLogger logger,
                           DeadEventHandler deadEventHandler, Interceptor interceptor, ExceptionHandler exHandler) {
        super(name, classInfoExtractor, logger, deadEventHandler, interceptor, exHandler);
    }

    @Override public synchronized void register(Object target) {
        super.register(target);
    }

    @Override public synchronized void unregister(Object target) {
        super.unregister(target);
    }

    @Override public synchronized void post(Object event) {
        super.post(event);
    }

    @Override public String toString() {
        return "Synchronized" + super.toString();
    }
}
