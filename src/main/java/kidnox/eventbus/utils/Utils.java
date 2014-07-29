package kidnox.eventbus.utils;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.async.AsyncDispatcherExt;
import kidnox.eventbus.async.SingleThreadWorker;
import kidnox.eventbus.async.Worker;

import java.lang.reflect.Method;
import java.util.Collection;

public final class Utils {

    public static <T> T checkNotNull(T instance) {
        if(instance == null) throw new NullPointerException();
        return instance;
    }

    public static boolean isNullOrEmpty(Object[] args) {
        return args == null || args.length == 0;
    }

    public static boolean notEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }

    public static Object invokeMethod(Object target, Method method, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Useful for testing.
     * */
    public static SingleThreadWorker getSingleThreadWorkerForName(String name, Dispatcher.Factory factory) {
        Dispatcher dispatcher = factory.getDispatcher(name);
        if(dispatcher instanceof AsyncDispatcherExt) {
            Worker worker = ((AsyncDispatcherExt)dispatcher).worker;
            if(worker instanceof SingleThreadWorker)
                return (SingleThreadWorker) worker;
        }
        return null;
    }

    //no instance
    private Utils() {}
}
