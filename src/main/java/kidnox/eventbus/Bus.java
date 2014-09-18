package kidnox.eventbus;

import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.extraction.ClassInfoExtractor;
import kidnox.eventbus.util.BusBuilder;

import static kidnox.eventbus.internal.InternalFactory.*;

public interface Bus {
    //logger tags
    String POST = "post";
    String PRODUCE = "produce";
    String INTERCEPT = "intercept";

    void register(Object target);

    void unregister(Object target);

    void post(Object event);


    public static final class Factory {

        public static Bus createBus(Dispatcher.Factory dispatcherFactory, ErrorHandler exHandler,
                                    DeadEventHandler deadEvHandler, EventLogger logger, EventInterceptor interceptor) {

            ClassInfoExtractor extractor = createClassInfoExtractor();
            dispatcherFactory = wrapFactoryWithCache(dispatcherFactory);
            //use stubs to prevent null checks
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
            return new BusBuilder();
        }

        private Factory() {}
    }
}
