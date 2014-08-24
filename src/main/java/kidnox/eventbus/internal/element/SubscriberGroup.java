package kidnox.eventbus.internal.element;

import kidnox.eventbus.internal.ElementsGroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SubscriberGroup implements ElementsGroup {

    final List<AsyncElement> subscribers;
    final Map<Class, Set<AsyncElement>> mapReference;

    public SubscriberGroup(List<AsyncElement> subscribers, Map<Class, Set<AsyncElement>> mapReference) {
        this.subscribers = subscribers;
        this.mapReference = mapReference;
    }

    @Override public void registerGroup(Object target) {

    }

    @Override public void unregisterGroup() {
        for (AsyncElement subscriber : subscribers) {
            mapReference.get(subscriber.eventType).remove(subscriber);
            subscriber.onUnregister();
        }
    }
}
