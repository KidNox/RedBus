package kidnox.eventbus.internal;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.extraction.ClassInfoExtractor;

import java.util.Map;

import static kidnox.eventbus.internal.Utils.newHashMap;

public final class InternalFactory {

    public static final Dispatcher CURRENT_THREAD_DISPATCHER = new Dispatcher() {

        @Override public boolean isDispatcherThread() {
            return true;
        }

        @Override public void dispatch(Runnable event) { }
    };

    public static ClassInfoExtractor createClassInfoExtractor() {
        return new ClassInfoExtractor.ClassInfoExtractorImpl();
    }

    public static Dispatcher.Factory getDefaultDispatcherFactory() {
        return new Dispatcher.Factory() {
            @Override public Dispatcher getDispatcher(String name) {
                if(name.isEmpty()) {
                    return InternalFactory.CURRENT_THREAD_DISPATCHER;
                } else {
                    throw new IllegalArgumentException("Dispatcher ["+name+"] not found");
                }
            }
        };
    }

    public static ErrorHandler getStubExHandler() {
        return new ErrorHandler() {
            @Override public boolean handle(Throwable thr, Object target, Object event) {
                return false;
            }
        };
    }

    public static DeadEventHandler getStubDeadEvHandler() {
        return new DeadEventHandler() {
            @Override public void onDeadEvent(Object event) { }
        };
    }

    public static EventLogger getStubLogger() {
        return new EventLogger() {
            @Override public void logEvent(Object event, Object target, String what) { }
        };
    }

    public static EventInterceptor getStubInterceptor() {
        return new EventInterceptor() {
            @Override public boolean intercept(Object event) {
                return false;
            }
        };
    }

    public static Dispatcher.Factory wrapFactoryWithCache(Dispatcher.Factory factory) {
        if(factory == null) return getDefaultDispatcherFactory();
        return new DispatchersFactoryCachedProxy(factory);
    }

    public static class DispatchersFactoryCachedProxy implements Dispatcher.Factory {

        final Map<String, Dispatcher> dispatchersMap = newHashMap(4);
        final Dispatcher.Factory dispatcherFactory;

        public DispatchersFactoryCachedProxy(Dispatcher.Factory dispatcherFactory) {
            this.dispatcherFactory = dispatcherFactory;
        }

        @Override public Dispatcher getDispatcher(String dispatcherName) {
            Dispatcher dispatcher = dispatchersMap.get(dispatcherName);
            if(dispatcher == null) {
                dispatcher = dispatcherFactory.getDispatcher(dispatcherName);
                if(dispatcher == null) {
                    if(dispatcherName.isEmpty()) {
                        dispatcher = InternalFactory.CURRENT_THREAD_DISPATCHER;
                    } else {
                        throw new IllegalArgumentException("Dispatcher["+dispatcherName+"] not found");
                    }
                }
                dispatchersMap.put(dispatcherName, dispatcher);
            }
            return dispatcher;
        }
    }

    private InternalFactory() {}
}
