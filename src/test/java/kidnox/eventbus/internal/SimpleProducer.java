package kidnox.eventbus.internal;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Producer;

@Producer
public class SimpleProducer {

    volatile int producedCount = 0;

    @Produce public Object produceObject() {
        producedCount++;
        return new Object();
    }

    public int getProducedCount() {
        return producedCount;
    }
}
