package kidnox.eventbus.internal;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Producer;

@Producer
public class SimpleProducer {

    volatile int producedCount = 0;

    @Produce public Event produceEvent() {
        producedCount++;
        return new Event();
    }

    public int getProducedCount() {
        return producedCount;
    }
}
