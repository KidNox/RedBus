package kidnox.eventbus;

import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.util.BusBuilder;

import static kidnox.eventbus.internal.InternalFactory.*;

public interface Bus {

    String POST = "post";
    String PRODUCE = "produce";
    String INTERCEPT = "intercept";

    void register(Object target); //TODO rename to connect?

    void unregister(Object target); //TODO rename to disconnect?

    void post(Object event);


    public static final class Factory {

        public static Bus createBus(EventDispatcher.Factory dispatcherFactory, ErrorHandler exHandler,
                                    DeadEventHandler deadEvHandler, EventLogger logger,
                                    EventInterceptor interceptor, boolean extraValidation) {

            ClassInfoExtractor extractor = createClassInfoExtractor(extraValidation);
            dispatcherFactory = wrapFactoryWithCache(dispatcherFactory);

            exHandler = exHandler == null ? getStubExHandler() : exHandler;
            deadEvHandler = deadEvHandler == null ? getStubDeadEvHandler() : deadEvHandler;
            logger = logger == null ? getStubLogger() : logger;
            interceptor = interceptor == null ? getStubInterceptor() : interceptor;

            return new AsyncBus(extractor, dispatcherFactory, exHandler, deadEvHandler, logger, interceptor);
        }

        public static Bus createDefault() {
            return builder().create();
        }

        public static BusBuilder builder() {
            return BusBuilder.get();
        }

        private Factory() { }
    }
}
