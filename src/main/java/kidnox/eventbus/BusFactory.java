package kidnox.eventbus;

import kidnox.eventbus.impl.BusDefaults;
import kidnox.eventbus.impl.BusImpl;
import kidnox.utils.Strings;

public final class BusFactory {

    static Bus createBus(String name, boolean async, AnnotationFinder annotationFinder,
                         DeadEventHandler deadEventHandler, EventLogger eventLogger) {
        final Bus bus = new BusImpl(name, annotationFinder, deadEventHandler);
        return async ? getSynchronizedDelegate(bus) : bus;
    }

    public static Bus getDefault() {
        return builder().create();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Bus getSynchronizedDelegate(final Bus bus) {
        return new Bus() {
            @Override
            public synchronized void register(Object target) {
                bus.register(target);
            }

            @Override
            public synchronized void unregister(Object target) {
                bus.unregister(target);
            }

            @Override
            public synchronized void post(Object event) {
                bus.post(event);
            }
        };
    }

    public static class Builder {
        boolean async = true;
        String name = Strings.EMPTY;

        DeadEventHandler deadEventHandler = null;
        AnnotationFinder annotationFinder = null;
        Dispatcher.Factory dispatcherFactory = null;
        ClassFilter classFilter = null;

        Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder singleThread() {
            this.async = false;
            return this;
        }

        public Builder withDeadEventHandler(DeadEventHandler deadEventHandler) {
            this.deadEventHandler = deadEventHandler;
            return this;
        }

        public Builder withAnnotationFinder(AnnotationFinder annotationFinder) {
            this.annotationFinder = annotationFinder;
            return this;
        }

        public Builder withDispatcherFactory(Dispatcher.Factory factory) {
            this.dispatcherFactory = factory;
            return this;
        }

        public Builder withClassFilter(ClassFilter classFilter) {
            this.classFilter = classFilter;
            return this;
        }

        public Bus create() {
            if (annotationFinder == null)
                annotationFinder = BusDefaults.createDefaultAnnotationFinder(classFilter, dispatcherFactory);
            return createBus(name, async, annotationFinder, deadEventHandler, null);
        }
    }

    //no instance
    private BusFactory() {
    }
}
