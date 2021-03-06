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
        super.registerGroup(target, bus);
        for(ElementInfo entry : classInfo.elements.values()) {
            final AsyncElement producer = new AsyncElement(target, entry, dispatcher);
            if(bus.putProducer(producer.elementInfo.eventType, producer) != null) {
                throwBusException("register", target, " producer for event "
                        + producer.elementInfo.eventType + " already registered");
            }
            producers.add(producer);
            final Set<AsyncElement> subscribers = bus.getSubscribers(producer.elementInfo.eventType);
            if(notEmpty(subscribers)) {
                bus.dispatch(producer);
            }
        }
    }

    @Override public void unregisterGroup(Object target, AsyncBus bus) {
        super.unregisterGroup(target, bus);
        for(AsyncElement producer : producers) {
            bus.removeProducer(producer.elementInfo.eventType);
            producer.onUnregister();
        }
    }
}
