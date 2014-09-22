package kidnox.eventbus.test;

import kidnox.eventbus.Handle;
import kidnox.eventbus.Subscriber;

@Subscriber
public class MutableEventHandler {

    volatile Event returnValue;
    volatile int handleCount;

    @Handle public Event handle(Event2 event) {
        handleCount++;
        return returnValue;
    }

    public void setReturnValue(Event returnValue) {
        this.returnValue = returnValue;
    }

    public int getHandleCount() {
        return handleCount;
    }

}
