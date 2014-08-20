package kidnox.eventbus.internal;

import kidnox.eventbus.*;
import kidnox.eventbus.impl.BusServiceImpl;
import kidnox.eventbus.impl.ClassInfoExtractorImpl;
import kidnox.eventbus.impl.ClassInfoExtractorValidation;

public final class InternalFactory {

    public static ClassInfoExtractor createClassInfoExtractor(boolean extraValidation) {
        if(extraValidation) return new ClassInfoExtractorValidation();
        else return new ClassInfoExtractorImpl();
    }

    public static BusService createBusService(EventDispatcher.Factory factory, EventLogger logger,
                                              DeadEventHandler deadEventHandler, Interceptor interceptor,
                                              ExceptionHandler exceptionHandler) {
        return new BusServiceImpl(factory, logger, deadEventHandler, interceptor, exceptionHandler);
    }

    public static final EventDispatcher CURRENT_THREAD_DISPATCHER = new EventDispatcher() {

        @Override public boolean isDispatcherThread() {
            return true;
        }

        @Override public void dispatch(Runnable event) {

        }
    };

    public static EventDispatcher.Factory createDefaultEventDispatcherFactory() {
        return new EventDispatcher.Factory() {
            @Override public EventDispatcher getDispatcher(String name) {
                if(name.isEmpty()) {
                    return InternalFactory.CURRENT_THREAD_DISPATCHER;
                } else {
                    throw new IllegalArgumentException("Dispatcher ["+name+"] not found");
                }
            }
        };
    }

    public static EventLogger getStubLogger() {
        return new EventLogger() {
            @Override public void logEvent(Object event, Object target, String what) { }
        };
    }

}
