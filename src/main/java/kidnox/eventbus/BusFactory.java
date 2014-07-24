package kidnox.eventbus;

import kidnox.common.Factory;
import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.impl.AsyncBusDelegate;
import kidnox.eventbus.impl.BusDefaults;
import kidnox.eventbus.impl.BusImpl;
import kidnox.utils.Strings;

public final class BusFactory {

    static final int NO_SYNC_BUS        = 1;
    static final int SYNCHRONIZED_BUS   = 2;
    static final int ASYNC_BUS          = 3;

    static final int DEFAULT_BUS = SYNCHRONIZED_BUS;

    static Bus createBus(String name, int type, ClassInfoExtractor classInfoExtractor,
                         DeadEventHandler deadEventHandler, EventLogger eventLogger) {
        return wrapBusForType(new BusImpl(name, classInfoExtractor, deadEventHandler), type);
    }

    static Bus wrapBusForType(Bus bus, int type) {
        switch (type) {
            case NO_SYNC_BUS:
                return bus;
            case SYNCHRONIZED_BUS:
                return getSynchronizedDelegate(bus);
            case ASYNC_BUS:
                return new AsyncBusDelegate(bus);
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
        String name = Strings.EMPTY;

        DeadEventHandler deadEventHandler = null;
        ClassInfoExtractor classInfoExtractor = null;
        Factory<Dispatcher, String> dispatcherFactory = null;
        ClassFilter classFilter = null;

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

        public Builder asyncBus() {
            this.type = ASYNC_BUS;
            return this;
        }

        public Builder withDeadEventHandler(DeadEventHandler deadEventHandler) {
            this.deadEventHandler = deadEventHandler;
            return this;
        }

        public Builder withDispatcherFactory(Factory<Dispatcher, String> factory) {
            this.dispatcherFactory = factory;
            return this;
        }

        public Builder withAndroidDefaultDispatchers() {
            this.dispatcherFactory = AsyncDispatcherFactory.getAndroidDispatcherFactory();
            return this;
        }

        public Builder withClassFilter(ClassFilter classFilter) {
            this.classFilter = classFilter;
            return this;
        }

        public Bus create() {
            classInfoExtractor = BusDefaults.createDefaultExtractor(classFilter, dispatcherFactory);
            return createBus(name, type, classInfoExtractor, deadEventHandler, null);
        }
    }

    public static Bus getSynchronizedDelegate(final Bus bus) {
        if(bus == null) throw new NullPointerException();
        return new Bus() {
            @Override public synchronized void register(Object target) {
                bus.register(target);
            }

            @Override public synchronized void unregister(Object target) {
                bus.unregister(target);
            }

            @Override public synchronized void post(Object event) {
                bus.post(event);
            }

            @Override public String toString() {
                return bus.toString();
            }
        };
    }

    //no instance
    private BusFactory() {
    }
}
