package kidnox.eventbus.async;

public class PackageLocalProvider {

    public static boolean inCurrentThread(AsyncDispatcherExt asyncDispatcherExt) {
        return asyncDispatcherExt.inCurrentThread();
    }

    public static SingleThreadWorker getSingleThreadWorker(AsyncDispatcherExt asyncDispatcherExt) {
        return (SingleThreadWorker) asyncDispatcherExt.worker;
    }

}
