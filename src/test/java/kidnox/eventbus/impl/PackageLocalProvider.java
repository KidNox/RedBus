package kidnox.eventbus.impl;

import java.util.List;

public class PackageLocalProvider {

    public static List<EventSubscriber> getSubscribers(BusImpl bus, Object target) {
        return bus.instanceToSubscribersMap.get(target);
    }

}
