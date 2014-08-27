package kidnox.eventbus.internal;

import kidnox.eventbus.internal.element.AsyncElement;
import kidnox.eventbus.internal.extraction.ClassInfoExtractor;
import kidnox.eventbus.internal.extraction.ClassInfoExtractorImpl;

import java.util.Map;
import java.util.Set;

public final class PackageLocalProvider {

    public static Set<AsyncElement> getSubscribers(AsyncBus bus, Class eventType) {
        return bus.eventTypeToSubscribersMap.get(eventType);
    }

}
