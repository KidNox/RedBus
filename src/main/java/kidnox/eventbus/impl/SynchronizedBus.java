package kidnox.eventbus.impl;

import kidnox.eventbus.internal.BusService;
import kidnox.eventbus.internal.ClassInfoExtractor;

public class SynchronizedBus extends BusImpl {

    public SynchronizedBus(String name, BusService busService, ClassInfoExtractor classInfoExtractor) {
        super(name, busService, classInfoExtractor);
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
