package kidnox.eventbus.internal.element;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ElementsGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static kidnox.eventbus.internal.Utils.notEmpty;
import static kidnox.eventbus.internal.Utils.throwBusException;

public final class ProducerGroup extends ElementsGroup {

    final List<AsyncElement> producers = new LinkedList<AsyncElement>();

    public ProducerGroup(ClassInfo classInfo, Dispatcher dispatcher) {
        super(classInfo, dispatcher);
    }

    @Override public void registerGroup(Object target, AsyncBus bus) {
        for(ElementInfo entry : classInfo.elements) {
            final AsyncElement producer = new AsyncElement(target, entry, dispatcher);
            if(bus.putProducer(producer.eventType, producer) != null) {
                throwBusException("register", target, " producer for event "
                        + producer.eventType + " already registered");
            }
            producers.add(producer);
            final Set<AsyncElement> subscribers = bus.getSubscribers(producer.eventType);
            if(notEmpty(subscribers)) {
                bus.dispatch(producer);
            }
        }
    }

    @Override public void unregisterGroup(AsyncBus bus) {
        for(AsyncElement producer : producers) {
            bus.removeProducer(producer.eventType);
            producer.onUnregister();
        }
    }
}
