package kidnox.eventbus;



public final class AsyncBus extends BusImpl {


    AsyncBus(String name, AnnotationFinder annotationFinder, DeadEventHandler deadEventHandler) {
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
