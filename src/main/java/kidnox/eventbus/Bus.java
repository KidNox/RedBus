package kidnox.eventbus;

import kidnox.eventbus.impl.AsyncBus;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.util.BusBuilder;

import static kidnox.eventbus.internal.InternalFactory.*;

public interface Bus {

    String POST = "post";
    String PRODUCE = "produce";
    String INTERCEPT = "intercept";

    void register(Object target);

    void unregister(Object target);

    void post(Object event);


    public static final class Factory {

        public static Bus createBus(EventDispatcher.Factory dispatcherFactory, ExceptionHandler exHandler,
                                    DeadEventHandler deadEvHandler, EventLogger logger,
                                    Interceptor interceptor, boolean extraValidation) {

            ClassInfoExtractor extractor = createClassInfoExtractor(extraValidation);

            dispatcherFactory = dispatcherFactory == null ? getDefaultDispatcherFactory() : dispatcherFactory;
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

        private Factory() {
        }
    }
}
