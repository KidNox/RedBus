package kidnox.eventbus.test;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.async.AsyncEventDispatcherExt;
import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.async.SingleThreadWorker;
import kidnox.eventbus.async.Worker;

public class TestUtils {
    
    public static void addDispatchersToFactory(AsyncDispatcherFactory factory, NamedAsyncEventDispatcher... dispatchers) {
        for (NamedAsyncEventDispatcher dispatcher : dispatchers) {
            factory.addDispatcher(dispatcher.getName(), dispatcher);
        }
    }

    /**
     * Useful for testing.
     * */
    public static SingleThreadWorker getSTWorkerForName(String name, EventDispatcher.Factory factory) {
        EventDispatcher dispatcher = factory.getDispatcher(name);
        if(dispatcher instanceof AsyncEventDispatcherExt) {
            Worker worker = ((AsyncEventDispatcherExt)dispatcher).worker;
            if(worker instanceof SingleThreadWorker)
                return (SingleThreadWorker) worker;
        }
        return null;
    }
    
}
