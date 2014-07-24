package kidnox.eventbus.async;

import android.os.Handler;
import android.os.Looper;

import kidnox.common.Factory;
import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.impl.AsyncDispatcher;

import java.util.HashMap;
import java.util.Map;

public class AsyncDispatcherFactory implements Factory<Dispatcher, String> {

    protected final Map<String, Dispatcher> dispatchersMap;

    public AsyncDispatcherFactory() {
        this(new HashMap<String, Dispatcher>());
    }

    public AsyncDispatcherFactory(Map<String, Dispatcher> map) {
        dispatchersMap = map;
    }

    @Override public Dispatcher get(String s) {
        Dispatcher dispatcher = dispatchersMap.get(s);
        if(dispatcher == null) throw new NullPointerException(String.format("dispatcher for %s is not registered", s));
        return dispatcher;
    }

    public AsyncDispatcherFactory addDispatcher(String key, Dispatcher dispatcher) {
        dispatchersMap.put(key, dispatcher);
        return this;
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
        };
    }

    public static Dispatcher getWorkerDispatcher() {
        return new AsyncDispatcherExt(new SingleThreadWorker(Dispatcher.WORKER));
    }

}
