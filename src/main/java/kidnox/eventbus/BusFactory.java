package kidnox.eventbus;

import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.impl.AsyncBus;
import kidnox.eventbus.impl.BusDefaults;
import kidnox.eventbus.impl.BusImpl;
import kidnox.eventbus.impl.SynchronizedBus;

public final class BusFactory {

    static final int NO_SYNC_BUS        = 1;
    static final int SYNCHRONIZED_BUS   = 2;
    static final int ASYNC_BUS          = 3;

    static final int DEFAULT_BUS = SYNCHRONIZED_BUS;

    static Bus createBus(String name, int type, ClassInfoExtractor classInfoExtractor,
                         DeadEventHandler deadEventHandler, EventLogger eventLogger) {
        switch (type) {
            case NO_SYNC_BUS:
                return new BusImpl(name, classInfoExtractor, eventLogger, deadEventHandler);
            case SYNCHRONIZED_BUS:
                return new SynchronizedBus(name, classInfoExtractor, eventLogger, deadEventHandler);
            case ASYNC_BUS:
                return new AsyncBus(name, classInfoExtractor, eventLogger, deadEventHandler);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Bus getDefault() {
        return builder().create();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        int type = DEFAULT_BUS;
        String name = "";
        boolean validate = false;

        EventLogger eventLogger = null;
        DeadEventHandler deadEventHandler = null;
        Dispatcher.Factory dispatcherFactory = null;

        ClassInfoExtractor classInfoExtractor = null;

        Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder notSynchronized() {
            this.type = NO_SYNC_BUS;
            return this;
        }

//        public Builder asyncBus() {
//            this.type = ASYNC_BUS;
//            return this;
//        }

        public Builder withEventLogger(EventLogger eventLogger) {
            this.eventLogger = eventLogger;
            return this;
        }

        public Builder withDeadEventHandler(DeadEventHandler deadEventHandler) {
            this.deadEventHandler = deadEventHandler;
            return this;
        }

        public Builder withDispatcherFactory(Dispatcher.Factory factory) {
            this.dispatcherFactory = factory;
            return this;
        }

        public Builder withAndroidDefaultDispatchers() {
            this.dispatcherFactory = AsyncDispatcherFactory.getAndroidDispatcherFactory();
            return this;
        }

        public Builder withValidation() {
            validate = true;
            return this;
        }

        public Bus create() {
            classInfoExtractor = validate ? BusDefaults.createValidationExtractor(dispatcherFactory)
                    : BusDefaults.createDefaultExtractor(dispatcherFactory);
            return createBus(name, type, classInfoExtractor, deadEventHandler, eventLogger);
        }
    }

    //no instance
    private BusFactory() {
    }
}
