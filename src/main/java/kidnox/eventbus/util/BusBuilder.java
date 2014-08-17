package kidnox.eventbus.util;

import kidnox.eventbus.*;
import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.internal.Utils;

public final class BusBuilder {

    String name = "";
    boolean validate = false;
    boolean singleThread = false;

    EventLogger eventLogger = null;
    DeadEventHandler deadEventHandler = null;
    Interceptor interceptor = null;
    ExceptionHandler exceptionHandler = null;
    EventDispatcher.Factory dispatcherFactory = null;

    ClassInfoExtractor classInfoExtractor = null;

    BusBuilder() {
    }

    public static BusBuilder get() {
        return new BusBuilder();
    }

    public BusBuilder withName(String name) {
        this.name = Utils.checkNotNull(name);
        return this;
    }

    public BusBuilder forSingleThread() {
        this.singleThread = true;
        return this;
    }

    public BusBuilder withEventLogger(EventLogger eventLogger) {
        this.eventLogger = Utils.checkNotNull(eventLogger);
        return this;
    }

    public BusBuilder withDeadEventHandler(DeadEventHandler deadEventHandler) {
        this.deadEventHandler = Utils.checkNotNull(deadEventHandler);
        return this;
    }

    public BusBuilder withInterceptor(Interceptor interceptor) {
        this.interceptor = Utils.checkNotNull(interceptor);
        return this;
    }

    public BusBuilder withExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public BusBuilder withEventDispatcherFactory(EventDispatcher.Factory factory) {
        this.dispatcherFactory = Utils.checkNotNull(factory);
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
        classInfoExtractor = InternalFactory.createClassInfoExtractor(validate);
        return Bus.Factory.createBus(name, singleThread, classInfoExtractor, dispatcherFactory, deadEventHandler,
                eventLogger, interceptor, exceptionHandler);
    }

}
