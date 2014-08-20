package kidnox.eventbus;

import kidnox.eventbus.impl.AsyncBus;
import kidnox.eventbus.internal.BusService;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.util.BusBuilder;

public interface Bus {

    String POST = "post";
    String PRODUCE = "produce";
    String INTERCEPT = "intercept";

    void register(Object target);

    void unregister(Object target);

    void post(Object event);


    public static final class Factory {

        public static Bus createBus(EventDispatcher.Factory dispatcherFactory, ExceptionHandler exceptionHandler,
                                    DeadEventHandler deadEventHandler, EventLogger eventLogger,
                                    Interceptor interceptor, boolean extraValidation) {
            BusService busService = InternalFactory.createBusService(dispatcherFactory, eventLogger,
                    deadEventHandler, interceptor, exceptionHandler);
            ClassInfoExtractor extractor = InternalFactory.createClassInfoExtractor(extraValidation);
            return new AsyncBus(busService, extractor);
        }

        public static Bus createDefault() {
            return builder().create();
        }

        public static BusBuilder builder() {
            return BusBuilder.get();
        }

        private Factory() {
        }
    }
}
