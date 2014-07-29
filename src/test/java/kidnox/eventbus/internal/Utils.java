package kidnox.eventbus.internal;

import kidnox.eventbus.async.AsyncDispatcherFactory;

public class Utils {
    
    public static void addDispatchersToFactory(AsyncDispatcherFactory factory, NamedAsyncDispatcher... dispatchers) {
        for (NamedAsyncDispatcher dispatcher : dispatchers) {
            factory.addDispatcher(dispatcher.getName(), dispatcher);
        }
    }
    
}
