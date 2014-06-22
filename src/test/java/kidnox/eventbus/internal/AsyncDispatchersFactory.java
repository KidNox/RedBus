package kidnox.eventbus.internal;

import kidnox.annotations.NotNull;
import kidnox.common.Factory;
import kidnox.eventbus.Dispatcher;

import java.util.HashMap;
import java.util.Map;

public class AsyncDispatchersFactory implements Factory<Dispatcher, String> {

    final Map<String, NamedAsyncDispatcher> dispatchers = new HashMap<String, NamedAsyncDispatcher>();

    @Override
    public Dispatcher get(@NotNull String dispatcherName) {
        return dispatchers.get(dispatcherName);
    }

    public void addDispatcher(NamedAsyncDispatcher dispatcher) {
        dispatchers.put(dispatcher.getName(), dispatcher);
    }
}
