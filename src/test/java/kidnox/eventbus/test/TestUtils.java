package kidnox.eventbus.test;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.util.AsyncDispatcherFactory;
import kidnox.eventbus.util.SingleThreadEventDispatcher;

public class TestUtils {
    
    public static void addDispatchersToFactory(AsyncDispatcherFactory factory, SingleThreadEventDispatcher... dispatchers) {
        for (SingleThreadEventDispatcher dispatcher : dispatchers) {
            factory.addDispatcher(dispatcher.name, dispatcher);
        }
    }

    public static SingleThreadEventDispatcher getSTWorkerForName(String name, Dispatcher.Factory factory) {
        Dispatcher dispatcher = factory.getDispatcher(name);
        if(dispatcher instanceof SingleThreadEventDispatcher) {
            return ((SingleThreadEventDispatcher)dispatcher);
        }
        return null;
    }
    
}
