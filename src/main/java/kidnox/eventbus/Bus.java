package kidnox.eventbus;

import kidnox.eventbus.impl.BusImpl;
import kidnox.eventbus.impl.SynchronizedBus;
import kidnox.eventbus.test.ClassInfoExtractor;
import kidnox.eventbus.util.BusBuilder;
import kidnox.eventbus.util.Utils;

public interface Bus {

    void register(Object target);

    void unregister(Object target);

    void post(Object event);


    public static final class Factory {

        public static Bus createBus(String name, boolean sync, ClassInfoExtractor extractor,
                             DeadEventHandler deadEventHandler, EventLogger eventLogger,
                             Interceptor interceptor, ExceptionHandler exHandler) {
            if(sync) {
                return new SynchronizedBus(name, extractor, eventLogger, deadEventHandler, interceptor, exHandler);
            } else {
                return new BusImpl(name, extractor, eventLogger, deadEventHandler, interceptor, exHandler);
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
