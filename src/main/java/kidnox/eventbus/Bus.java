package kidnox.eventbus;

import kidnox.eventbus.impl.AsyncBus;
import kidnox.eventbus.internal.BusService;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.util.BusBuilder;

public interface Bus {

    String POST         = "post";
    String PRODUCE      = "produce";
    String INTERCEPT    = "intercept";

    void register(Object target);

    void unregister(Object target);

    void post(Object event);


    public static final class Factory {

        public static Bus createBus(String name, ClassInfoExtractor extractor,
                             EventDispatcher.Factory dispatcherFactory, DeadEventHandler deadEventHandler,
                             EventLogger eventLogger, Interceptor interceptor, ExceptionHandler exHandler) {
            BusService busService = InternalFactory.createBusService(dispatcherFactory, eventLogger,
                    deadEventHandler, interceptor, exHandler);
            return new AsyncBus(name, busService, extractor);
        }

        public static Bus createDefault() {
            return builder().create();
        }

        public static BusBuilder builder() {
            return BusBuilder.get();
        }

        //no instance
        private Factory() {}
    }
}
