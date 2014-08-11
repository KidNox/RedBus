package kidnox.eventbus.test;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Producer;

@Producer
public class EventsProducer {

    private volatile int producedEventsCount;
    private volatile int producedEvents2Count;

    @Produce public Event produceEvent() {
        producedEventsCount++;
        return new Event();
    }

    @Produce public Event2 produceEvent2() {
        producedEvents2Count++;
        return new Event2();
    }

    public int getProducedEventsCount() {
        return producedEventsCount;
    }

    public int getProducedEvents2Count() {
        return producedEvents2Count;
    }
}
