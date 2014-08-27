package kidnox.eventbus.test.bad;

import kidnox.eventbus.Subscriber;
import kidnox.eventbus.Handle;
import kidnox.eventbus.test.Event;

@Subscriber
public class BadProcessor3 {

    @Handle
    public Event processEvent(Event event) {
        return event;
    }

}
