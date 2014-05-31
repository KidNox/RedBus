package kidnox.eventbus;

import kidnox.common.utils.Strings;

public class BusFactory {

    static Bus createBus(Builder builder) {
        if (builder.async) {
            return new AsyncBus(builder.name, builder.annotationFinder, builder.deadEventHandler);
        } else {
            return new BusImpl(builder.name, builder.annotationFinder, builder.deadEventHandler);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        boolean async = false;
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

        public Builder async() {
            this.async = true;
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
                annotationFinder = new AnnotationFinder.DefaultAnnotationFinder(classFilter, dispatcherFactory);
            return createBus(this);
        }
    }


    private BusFactory() {
    }
}
