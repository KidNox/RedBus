package kidnox.eventbus.test;

import kidnox.eventbus.Dispatcher;

import java.util.HashMap;
import java.util.Map;

public class MappedDispatchersFactory implements Dispatcher.Factory {

    final Map<String, Dispatcher> map = new HashMap<String, Dispatcher>();

    private volatile int getDispatcherCalledCount;

    @Override public Dispatcher getDispatcher(String name) {
        getDispatcherCalledCount++;
        Dispatcher dispatcher = map.get(name);
        if(dispatcher == null) throw new NullPointerException();
        return null;
    }

    public void putDispatcher(String key, Dispatcher dispatcher) {
        map.put(key, dispatcher);
    }

    public int getDispatcherCalledCount() {
        return getDispatcherCalledCount;
    }
}
