package kidnox.eventbus.util;

import android.os.Handler;
import android.os.Looper;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.InternalFactory;

import java.util.HashMap;
import java.util.Map;

import static kidnox.eventbus.internal.Utils.checkNotNull;

public class AsyncDispatcherFactory implements Dispatcher.Factory {//TODO refactor, lazy initialization

    protected final Map<String, Dispatcher> dispatchersMap;
    protected final Thread.UncaughtExceptionHandler exceptionHandler;

    public AsyncDispatcherFactory(String... dispatchers) {
        this();
        registerDispatchersForNames(dispatchers);
    }

    public AsyncDispatcherFactory() {
        this((Thread.UncaughtExceptionHandler)null);
    }

    public AsyncDispatcherFactory(Thread.UncaughtExceptionHandler exceptionHandler) {
        this(new HashMap<String, Dispatcher>(), exceptionHandler);
    }

    public AsyncDispatcherFactory(Map<String, Dispatcher> map, Thread.UncaughtExceptionHandler exceptionHandler) {
        dispatchersMap = map;
        this.exceptionHandler = exceptionHandler;
    }

    @Override public Dispatcher getDispatcher(String name) {
        Dispatcher dispatcher = dispatchersMap.get(name);
        if(dispatcher == null) {
            if(name.isEmpty()) {
                return InternalFactory.CURRENT_THREAD_DISPATCHER;
            } else {
                throw new IllegalArgumentException("Dispatcher ["+name+"] not found");
            }
        }
        return dispatcher;
    }

    public AsyncDispatcherFactory addDispatcher(String key, Dispatcher dispatcher) {
        dispatchersMap.put(checkNotNull(key), checkNotNull(dispatcher));
        return this;
    }

    public AsyncDispatcherFactory registerDispatcherForName(String name) {
        dispatchersMap.put(name, createAsyncDispatcher(name, exceptionHandler));
        return this;
    }

    public AsyncDispatcherFactory registerDispatchersForNames(String... names) {
        for(String name : names) {
            dispatchersMap.put(name, createAsyncDispatcher(name, exceptionHandler));
        }
        return this;
    }

    public static Dispatcher createAsyncDispatcher() {
        return new SingleThreadEventDispatcher();
    }

    public static Dispatcher createAsyncDispatcher(String name, Thread.UncaughtExceptionHandler exceptionHandler) {
        return new SingleThreadEventDispatcher().withUncaughtExceptionHandler(exceptionHandler);
    }

    public static Dispatcher getWorkerDispatcher() {
        return createAsyncDispatcher();
    }

    public static AsyncDispatcherFactory getAndroidDispatcherFactory() {
        return new AsyncDispatcherFactory(getAndroidDefaultDispatchersMap(), null);
    }

    public static Map<String, Dispatcher> getAndroidDefaultDispatchersMap() {
        Map<String, Dispatcher> map = new HashMap<String, Dispatcher>();
        map.put(Dispatcher.MAIN, getAndroidMainDispatcher());
        map.put(Dispatcher.WORKER, getWorkerDispatcher());
        return map;
    }

    public static Dispatcher getAndroidMainDispatcher() {
        return new Dispatcher() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @Override public void dispatch(Runnable runnable) {
                handler.post(runnable);
            }

            @Override public boolean isDispatcherThread() {
                return Looper.myLooper() == Looper.getMainLooper();
            }
        };
    }

}
