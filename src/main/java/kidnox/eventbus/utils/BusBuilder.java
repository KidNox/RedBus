package kidnox.eventbus.utils;

import kidnox.eventbus.*;
import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.impl.BusDefaults;

import static kidnox.eventbus.impl.BusDefaults.*;

public final class BusBuilder {

    int type = DEFAULT_BUS;
    String name = "";
    boolean validate = false;

    EventLogger eventLogger = null;
    DeadEventHandler deadEventHandler = null;
    Interceptor interceptor = null;
    Dispatcher.Factory dispatcherFactory = null;

    ClassInfoExtractor classInfoExtractor = null;

    BusBuilder() {
    }

    public BusBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public BusBuilder notSynchronized() {
        this.type = NO_SYNC_BUS;
        return this;
    }

//        public Builder asyncBus() {
//            this.type = ASYNC_BUS;
//            return this;
//        }

    public BusBuilder withEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
        return this;
    }

    public BusBuilder withDeadEventHandler(DeadEventHandler deadEventHandler) {
        this.deadEventHandler = deadEventHandler;
        return this;
    }

    public BusBuilder withInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public BusBuilder withDispatcherFactory(Dispatcher.Factory factory) {
        this.dispatcherFactory = factory;
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
        classInfoExtractor = validate ? BusDefaults.createValidationExtractor(dispatcherFactory)
                : BusDefaults.createDefaultExtractor(dispatcherFactory);
        return Bus.Factory.createBus(name, type, classInfoExtractor, deadEventHandler, eventLogger, interceptor);
    }

}
