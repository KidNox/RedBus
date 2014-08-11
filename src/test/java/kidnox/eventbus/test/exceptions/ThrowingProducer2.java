package kidnox.eventbus.test.exceptions;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Producer;
import kidnox.eventbus.test.Event2;

@Producer
public class ThrowingProducer2 {

    volatile int producedCount = 0;

    @Produce public Event2 produceEvent() throws TestException {
        producedCount++;
        throw new TestException();
    }

    public int getProducedCount() {
        return producedCount;
    }
}
