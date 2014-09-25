package kidnox.eventbus.test;

import kidnox.eventbus.Dispatcher;

import java.util.HashMap;
import java.util.Map;

public class MappedDispatchersFactory implements Dispatcher.Factory {

    Map<String, Dispatcher> map = new HashMap<String, Dispatcher>();

    @Override public Dispatcher getDispatcher(String name) {
        Dispatcher dispatcher = map.get(name);
        if(dispatcher == null) throw new NullPointerException();
        return null;
    }

    public void putDispatcher(String key, Dispatcher dispatcher) {
        map.put(key, dispatcher);
    }
}
