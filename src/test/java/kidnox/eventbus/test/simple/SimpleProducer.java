package kidnox.eventbus.test.simple;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Producer;
import kidnox.eventbus.test.Event;

@Producer
public class SimpleProducer {

    volatile int producedCount = 0;

    @Produce public Event produceEvent() {
        producedCount++;
        return obtainEvent();
    }

    protected Event obtainEvent() {
        return new Event();
    }

    public int getProducedCount() {
        return producedCount;
    }
}
