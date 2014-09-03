package kidnox.eventbus.internal.element;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ElementsGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static kidnox.eventbus.internal.Utils.newHashSet;

public final class SubscriberGroup extends ElementsGroup {

    final List<AsyncElement> subscribers = new LinkedList<AsyncElement>();

    public SubscriberGroup(ClassInfo classInfo, Dispatcher dispatcher) {
        super(classInfo, dispatcher);
    }

    @Override public void registerGroup(Object target, AsyncBus bus) {
        final boolean checkProducers = bus.checkProducers();
        for(ElementInfo entry : classInfo.elements) {
            final AsyncElement subscriber = new AsyncElement(target, entry, dispatcher);
            subscribers.add(subscriber);
            if(checkProducers) {
                AsyncElement producer = bus.getProducer(subscriber.eventType);
                if(producer != null) {
                    bus.dispatch(producer, subscriber);
                }
            }
            Set<AsyncElement> set = bus.getSubscribers(subscriber.eventType);
            if (set == null) {
                set = newHashSet(2);
                bus.putSubscribers(subscriber.eventType, set);
            }
            set.add(subscriber);
        }
    }

    @Override public void unregisterGroup(AsyncBus bus) {
        for (AsyncElement subscriber : subscribers) {
            bus.getSubscribers(subscriber.eventType).remove(subscriber);
            subscriber.onUnregister();
        }
    }
}
