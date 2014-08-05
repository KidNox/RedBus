package kidnox.eventbus.internal;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.async.AsyncDispatcherExt;
import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.async.SingleThreadWorker;
import kidnox.eventbus.async.Worker;

public class TestUtils {
    
    public static void addDispatchersToFactory(AsyncDispatcherFactory factory, NamedAsyncDispatcher... dispatchers) {
        for (NamedAsyncDispatcher dispatcher : dispatchers) {
            factory.addDispatcher(dispatcher.getName(), dispatcher);
        }
    }

    /**
     * Useful for testing.
     * */
    public static SingleThreadWorker getSTWorkerForName(String name, Dispatcher.Factory factory) {
        Dispatcher dispatcher = factory.getDispatcher(name);
        if(dispatcher instanceof AsyncDispatcherExt) {
            Worker worker = ((AsyncDispatcherExt)dispatcher).worker;
            if(worker instanceof SingleThreadWorker)
                return (SingleThreadWorker) worker;
        }
        return null;
    }
    
}
