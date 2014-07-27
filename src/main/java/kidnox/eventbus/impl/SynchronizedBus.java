package kidnox.eventbus.impl;

import kidnox.eventbus.ClassInfoExtractor;
import kidnox.eventbus.DeadEventHandler;
import kidnox.eventbus.EventLogger;

public class SynchronizedBus extends BusImpl {

    public SynchronizedBus(String name, ClassInfoExtractor classInfoExtractor,
                           EventLogger logger, DeadEventHandler deadEventHandler) {
        super(name, classInfoExtractor, logger, deadEventHandler);
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
