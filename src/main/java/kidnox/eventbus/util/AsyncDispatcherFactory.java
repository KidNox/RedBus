package kidnox.eventbus.util;

import android.os.Handler;
import android.os.Looper;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.InternalFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static kidnox.eventbus.internal.Utils.checkNotNull;

public final class AsyncDispatcherFactory implements Dispatcher.Factory {//TODO refactor, lazy initialization

    protected final Map<String, Dispatcher> dispatchersMap;

    public AsyncDispatcherFactory() {
        this(new HashMap<String, Dispatcher>());
    }

    public AsyncDispatcherFactory(String... dispatchers) {
        this();
        withDispatchers(dispatchers);
    }

    public AsyncDispatcherFactory(Map<String, Dispatcher> map) {
        dispatchersMap = map;
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

    public AsyncDispatcherFactory addAsyncDispatcher(String key, Executor executor) {
        return addDispatcher(key, createDispatcher(executor));
    }

    public AsyncDispatcherFactory withDispatcher(String name) {
        dispatchersMap.put(name, createDispatcher(name));
        return this;
    }

    public AsyncDispatcherFactory withDispatchers(String... names) {
        for(String name : names) {
            dispatchersMap.put(name, createDispatcher(name));
        }
        return this;
    }

    public AsyncDispatcherFactory withAndroidMainDispatcher() {
        addDispatcher("main", getAndroidMainDispatcher());
        return this;
    }

    public static Dispatcher createDispatcher(String name) {
        return new SingleThreadEventDispatcher(name);
    }

    public static Dispatcher createDispatcher(final Executor executor) {
        return new Dispatcher() {
            @Override public boolean isDispatcherThread() {
                return false;
            }

            @Override public void dispatch(Runnable event) {
                executor.execute(event);
            }
        };
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
