package kidnox.eventbus.impl;

import kidnox.annotations.Internal;
import kidnox.eventbus.AnnotationFinder;
import kidnox.eventbus.DeadEventHandler;

@Internal
public final class AsyncBus extends BusImpl {

    public AsyncBus(String name, AnnotationFinder annotationFinder, DeadEventHandler deadEventHandler) {
        super(name, annotationFinder, deadEventHandler);
    }

    @Override
    public synchronized void register(Object target) {
        super.register(target);
    }

    @Override
    public synchronized void unregister(Object target) {
        super.unregister(target);
    }

    @Override
    public synchronized void post(Object event) {
        super.post(event);
    }


}
