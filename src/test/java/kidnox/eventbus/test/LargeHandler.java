package kidnox.eventbus.test;

import kidnox.eventbus.*;
import kidnox.eventbus.Handle;

import java.util.Date;

@Subscriber
public class LargeHandler {

    volatile int producedObjectCount = 0;
    volatile int producedEventCount = 0;
    volatile int producedStringCount = 0;
    volatile int producedDateCount = 0;

    @Handle public Object produceObject(Integer event) {
        producedObjectCount++;
        return new Object();
    }

    @Handle public Event produceEvent(Long event) {
        producedEventCount++;
        return new Event();
    }

    @Handle public String produceString(Double event) {
        producedStringCount++;
        return "string-event";
    }

    @Handle public Date produceDate(Float event) {
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
