package kidnox.eventbus;

import kidnox.eventbus.impl.AsyncBus;
import kidnox.eventbus.impl.BusImpl;
import kidnox.eventbus.impl.SynchronizedBus;
import kidnox.eventbus.utils.BusBuilder;
import kidnox.eventbus.utils.Utils;

import static kidnox.eventbus.impl.BusDefaults.*;

public interface Bus {

    void register(Object target);

    void unregister(Object target);

    void post(Object event);


    public static final class Factory {

        public static Bus createBus(String name, int type, ClassInfoExtractor classInfoExtractor,
                             DeadEventHandler deadEventHandler, EventLogger eventLogger, Interceptor interceptor) {
            switch (type) {
                case NO_SYNC_BUS:
                    return new BusImpl(name, classInfoExtractor, eventLogger, deadEventHandler, interceptor);
                case SYNCHRONIZED_BUS:
                    return new SynchronizedBus(name, classInfoExtractor, eventLogger, deadEventHandler, interceptor);
                case ASYNC_BUS:
                    return new AsyncBus(name, classInfoExtractor, eventLogger, deadEventHandler, interceptor);
                default:
                    throw new IllegalArgumentException();
            }
        }

        public static Bus createDefault() {
            return builder().create();
        }

        public static BusBuilder builder() {
            return Utils.getBuilder();
        }

        //no instance
        private Factory() {}
    }
}
