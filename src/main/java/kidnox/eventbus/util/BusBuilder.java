package kidnox.eventbus.util;

import kidnox.eventbus.*;
import kidnox.eventbus.async.AsyncDispatcherFactory;

import static kidnox.eventbus.internal.Utils.checkNotNull;

public final class BusBuilder {

    boolean extraValidation;

    EventLogger eventLogger;
    DeadEventHandler deadEventHandler;
    Interceptor interceptor;
    ExceptionHandler exceptionHandler;
    EventDispatcher.Factory dispatcherFactory;

    BusBuilder() {
    }

    public static BusBuilder get() {
        return new BusBuilder();
    }

    public BusBuilder withEventLogger(EventLogger eventLogger) {
        this.eventLogger = checkNotNull(eventLogger);
        return this;
    }

    public BusBuilder withDeadEventHandler(DeadEventHandler deadEventHandler) {
        this.deadEventHandler = checkNotNull(deadEventHandler);
        return this;
    }

    public BusBuilder withInterceptor(Interceptor interceptor) {
        this.interceptor = checkNotNull(interceptor);
        return this;
    }

    public BusBuilder withExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = checkNotNull(exceptionHandler);
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

    public BusBuilder withExtraValidation() {
        extraValidation = true;
        return this;
    }

    public Bus create() {
        return Bus.Factory.createBus(dispatcherFactory, exceptionHandler, deadEventHandler, eventLogger,
                interceptor, extraValidation);
    }

}
