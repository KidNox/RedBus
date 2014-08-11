package kidnox.eventbus.test;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Producer;

import java.util.Date;

@Producer
public class LargeProducer {

    volatile int producedObjectCount = 0;
    volatile int producedEventCount = 0;
    volatile int producedStringCount = 0;
    volatile int producedDateCount = 0;

    @Produce public Object produceObject() {
        producedObjectCount++;
        return new Object();
    }

    @Produce public Event produceEvent() {
        producedEventCount++;
        return new Event();
    }

    @Produce public String produceString() {
        producedStringCount++;
        return "string-event";
    }

    @Produce public Date produceDate() {
        producedDateCount++;
        return new Date();
    }

    public Object stubMethod() {
        return null;
    }

    public Event stubMethod2() {
        return null;
    }

    public int getProducedObjectCount() {
        return producedObjectCount;
    }

    public int getProducedEventCount() {
        return producedEventCount;
    }

    public int getProducedStringCount() {
        return producedStringCount;
    }

    public int getProducedDateCount() {
        return producedDateCount;
    }
}
