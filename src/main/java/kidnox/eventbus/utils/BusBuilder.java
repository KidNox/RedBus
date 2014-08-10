package kidnox.eventbus.utils;

import kidnox.eventbus.*;
import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;

import static kidnox.eventbus.utils.Utils.checkNotNull;

public final class BusBuilder {

    String name = "";
    boolean validate = false;
    boolean sync = true;

    EventLogger eventLogger = null;
    DeadEventHandler deadEventHandler = null;
    Interceptor interceptor = null;
    ExceptionHandler exceptionHandler = null;
    EventDispatcher.Factory dispatcherFactory = null;

    ClassInfoExtractor classInfoExtractor = null;

    BusBuilder() {
    }

    public BusBuilder withName(String name) {
        this.name = checkNotNull(name);
        return this;
    }

    public BusBuilder notSynchronized() {
        this.sync = false;
        return this;
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
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public BusBuilder withDispatcherFactory(EventDispatcher.Factory factory) {
        this.dispatcherFactory = checkNotNull(factory);
        return this;
    }

    public BusBuilder withAndroidDefaultDispatchers() {
        this.dispatcherFactory = AsyncDispatcherFactory.getAndroidDispatcherFactory();
        return this;
    }

    public BusBuilder withValidation() {
        validate = true;
        return this;
    }

    public Bus create() {
        classInfoExtractor = InternalFactory.createClassInfoExtractor(dispatcherFactory, validate);
        return Bus.Factory.createBus(name, sync, classInfoExtractor, deadEventHandler,
                eventLogger, interceptor, exceptionHandler);
    }

}
