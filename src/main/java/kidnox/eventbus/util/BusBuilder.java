package kidnox.eventbus.util;

import kidnox.eventbus.*;
import kidnox.eventbus.EventInterceptor;

import static kidnox.eventbus.internal.Utils.checkNotNull;

public final class BusBuilder {

    boolean extraValidation;

    EventLogger eventLogger;
    DeadEventHandler deadEventHandler;
    EventInterceptor interceptor;
    ErrorHandler errorHandler;
    EventDispatcher.Factory dispatcherFactory;

    BusBuilder() {
    }

    public static BusBuilder get() {
        return new BusBuilder();
    }

    public BusBuilder withEventLogger(EventLogger busLogger) {
        this.eventLogger = checkNotNull(busLogger);
        return this;
    }

    public BusBuilder withDeadEventHandler(DeadEventHandler deadEventHandler) {
        this.deadEventHandler = checkNotNull(deadEventHandler);
        return this;
    }

    public BusBuilder withInterceptor(EventInterceptor interceptor) {
        this.interceptor = checkNotNull(interceptor);
        return this;
    }

    public BusBuilder withExceptionHandler(ErrorHandler errorHandler) {
        this.errorHandler = checkNotNull(errorHandler);
        return this;
    }

    public BusBuilder withEventDispatcherFactory(EventDispatcher.Factory factory) {
        this.dispatcherFactory = checkNotNull(factory);
        return this;
    }

    public BusBuilder withAndroidDefaultDispatchers() {
        this.dispatcherFactory = AsyncDispatcherFactory.getAndroidDispatcherFactory();
        return this;
    }

    /**
     * Use for debugging only, first class registration in this mode may be several times slower
     * */
    public BusBuilder withExtraValidation() {
        extraValidation = true;
        return this;
    }

    public Bus create() {
        return Bus.Factory.createBus(dispatcherFactory, errorHandler, deadEventHandler, eventLogger,
                interceptor, extraValidation);
    }

}
