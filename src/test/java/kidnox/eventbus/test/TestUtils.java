package kidnox.eventbus.test;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.util.AsyncDispatcherFactory;
import kidnox.eventbus.util.SingleThreadEventDispatcher;

public class TestUtils {
    
    public static void addDispatchersToFactory(AsyncDispatcherFactory factory, NamedAsyncEventDispatcher... dispatchers) {
        for (NamedAsyncEventDispatcher dispatcher : dispatchers) {
            factory.addDispatcher(dispatcher.getName(), dispatcher);
        }
    }

    public static SingleThreadEventDispatcher getSTWorkerForName(String name, EventDispatcher.Factory factory) {
        EventDispatcher dispatcher = factory.getDispatcher(name);
        if(dispatcher instanceof SingleThreadEventDispatcher) {
            return ((SingleThreadEventDispatcher)dispatcher);
        }
        return null;
    }
    
}
