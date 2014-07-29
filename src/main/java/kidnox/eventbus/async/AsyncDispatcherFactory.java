package kidnox.eventbus.async;

import android.os.Handler;
import android.os.Looper;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.impl.AsyncDispatcher;
import kidnox.eventbus.impl.BusDefaults;

import java.util.HashMap;
import java.util.Map;

public class AsyncDispatcherFactory implements Dispatcher.Factory {

    protected final Map<String, Dispatcher> dispatchersMap;

    public AsyncDispatcherFactory(String... dispatchers) {
        this();
        registerDispatchersForNames(dispatchers);
    }

    public AsyncDispatcherFactory() {
        this(new HashMap<String, Dispatcher>());
    }

    public AsyncDispatcherFactory(Map<String, Dispatcher> map) {
        dispatchersMap = map;
    }

    @Override public Dispatcher getDispatcher(String name) {
        Dispatcher dispatcher = dispatchersMap.get(name);
        if(dispatcher == null) {
            if(name.isEmpty()) {
                return BusDefaults.CURRENT_THREAD_DISPATCHER;
            } else {
                throw new IllegalArgumentException("Dispatcher ["+name+"] not found");
            }
        }
        return dispatcher;
    }

    public AsyncDispatcherFactory addDispatcher(String key, Dispatcher dispatcher) {
        dispatchersMap.put(key, dispatcher);
        return this;
    }

    public AsyncDispatcherFactory registerDispatcherForName(String name) {
        dispatchersMap.put(name, createAsyncDispatcher(name));
        return this;
    }

    public AsyncDispatcherFactory registerDispatchersForNames(String... names) {
        for(String name : names) {
            dispatchersMap.put(name, createAsyncDispatcher(name));
        }
        return this;
    }

    public static Dispatcher createAsyncDispatcher(String name) {
        return new AsyncDispatcherExt(new SingleThreadWorker(name));
    }

    public static Dispatcher getWorkerDispatcher() {
        return createAsyncDispatcher(Dispatcher.WORKER);
    }

    public static AsyncDispatcherFactory getAndroidDispatcherFactory() {
        return new AsyncDispatcherFactory(getAndroidDefaultDispatchersMap());
    }

    public static Map<String, Dispatcher> getAndroidDefaultDispatchersMap() {
        Map<String, Dispatcher> map = new HashMap<String, Dispatcher>();
        map.put(Dispatcher.MAIN, getAndroidMainDispatcher());
        map.put(Dispatcher.WORKER, getWorkerDispatcher());
        return map;
    }

    public static Dispatcher getAndroidMainDispatcher() {
        return new AsyncDispatcher() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @Override protected void dispatch(Runnable runnable) {
                handler.post(runnable);
            }

            @Override protected boolean inCurrentThread() {
                return Looper.myLooper() == Looper.getMainLooper();
            }
        };
    }

}
