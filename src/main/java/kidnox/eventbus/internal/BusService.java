package kidnox.eventbus.internal;

import kidnox.eventbus.elements.EventProducer;
import kidnox.eventbus.elements.EventSubscriber;

import java.util.List;

public interface BusService {

    List<EventSubscriber> registerSubscribers(Object target, ClassInfo classInfo);

    List<EventProducer> registerProducers(Object target, ClassInfo classInfo);

    void unregisterSubscribers(List<EventSubscriber> subscribers);

    void unregisterProducers(List<EventProducer> producers);

    void post(Object event);

}
