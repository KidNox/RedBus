package kidnox.eventbus.internal;

import kidnox.annotations.NotNull;
import kidnox.eventbus.Dispatcher;

import java.util.HashMap;
import java.util.Map;

public class AsyncDispatchersFactory implements Dispatcher.Factory {

    final Map<String, NamedAsyncDispatcher> dispatchers = new HashMap<String, NamedAsyncDispatcher>();

    @Override
    public Dispatcher getDispatcher(@NotNull String dispatcherName) {
        return dispatchers.get(dispatcherName);
    }

    public void addDispatcher(NamedAsyncDispatcher dispatcher) {
        dispatchers.put(dispatcher.getName(), dispatcher);
    }
}
