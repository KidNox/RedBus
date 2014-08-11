package kidnox.eventbus.test.exceptions;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Producer;
import kidnox.eventbus.test.Event;

@Producer
public class ThrowingProducer {

    volatile int producedCount = 0;

    @Produce public Event produceEvent() {
        producedCount++;
        throw new RuntimeException();
    }

    public int getProducedCount() {
        return producedCount;
    }
}
