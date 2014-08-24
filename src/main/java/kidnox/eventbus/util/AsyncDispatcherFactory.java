package kidnox.eventbus.util;

import android.os.Handler;
import android.os.Looper;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.internal.InternalFactory;

import java.util.HashMap;
import java.util.Map;

import static kidnox.eventbus.internal.Utils.checkNotNull;

public class AsyncDispatcherFactory implements EventDispatcher.Factory {//TODO refactor, lazy initialization

    protected final Map<String, EventDispatcher> dispatchersMap;
    protected final Thread.UncaughtExceptionHandler exceptionHandler;

    public AsyncDispatcherFactory(String... dispatchers) {
        this();
        registerDispatchersForNames(dispatchers);
    }

    public AsyncDispatcherFactory() {
        this((Thread.UncaughtExceptionHandler)null);
    }

    public AsyncDispatcherFactory(Thread.UncaughtExceptionHandler exceptionHandler) {
        this(new HashMap<String, EventDispatcher>(), exceptionHandler);
    }

    public AsyncDispatcherFactory(Map<String, EventDispatcher> map, Thread.UncaughtExceptionHandler exceptionHandler) {
        dispatchersMap = map;
        this.exceptionHandler = exceptionHandler;
    }

    @Override public EventDispatcher getDispatcher(String name) {
        EventDispatcher dispatcher = dispatchersMap.get(name);
        if(dispatcher == null) {
            if(name.isEmpty()) {
                return InternalFactory.CURRENT_THREAD_DISPATCHER;
            } else {
                throw new IllegalArgumentException("Dispatcher ["+name+"] not found");
            }
        }
        return dispatcher;
    }

    public AsyncDispatcherFactory addDispatcher(String key, EventDispatcher dispatcher) {
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

    public static EventDispatcher createAsyncDispatcher() {
        return new SingleThreadEventDispatcher();
    }

    public static EventDispatcher createAsyncDispatcher(String name, Thread.UncaughtExceptionHandler exceptionHandler) {
        return new SingleThreadEventDispatcher().withUncaughtExceptionHandler(exceptionHandler);
    }

    public static EventDispatcher getWorkerDispatcher() {
        return createAsyncDispatcher();
    }

    public static AsyncDispatcherFactory getAndroidDispatcherFactory() {
        return new AsyncDispatcherFactory(getAndroidDefaultDispatchersMap(), null);
    }

    public static Map<String, EventDispatcher> getAndroidDefaultDispatchersMap() {
        Map<String, EventDispatcher> map = new HashMap<String, EventDispatcher>();
        map.put(EventDispatcher.MAIN, getAndroidMainDispatcher());
        map.put(EventDispatcher.WORKER, getWorkerDispatcher());
        return map;
    }

    public static EventDispatcher getAndroidMainDispatcher() {
        return new EventDispatcher() {
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
