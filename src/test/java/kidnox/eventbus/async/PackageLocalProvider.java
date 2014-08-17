package kidnox.eventbus.async;

public class PackageLocalProvider {

    public static boolean inCurrentThread(AsyncEventDispatcherExt asyncDispatcherExt) {
        return asyncDispatcherExt.isDispatcherThread();
    }

    public static SingleThreadWorker getSingleThreadWorker(AsyncEventDispatcherExt asyncDispatcherExt) {
        return (SingleThreadWorker) asyncDispatcherExt.worker;
    }

}
