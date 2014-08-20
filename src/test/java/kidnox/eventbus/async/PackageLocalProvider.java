package kidnox.eventbus.async;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.util.SingleThreadEventDispatcher;

public class PackageLocalProvider {

    public static SingleThreadEventDispatcher getSingleThreadWorker(EventDispatcher eventDispatcher) {
        return (SingleThreadEventDispatcher) eventDispatcher;
    }

}
