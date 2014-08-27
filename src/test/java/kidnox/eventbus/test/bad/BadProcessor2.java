package kidnox.eventbus.test.bad;

import kidnox.eventbus.*;
import kidnox.eventbus.Handle;
import kidnox.eventbus.test.Event;

@Subscriber
public class BadProcessor2 {

    @Handle
    public Event process() {
        return null;
    }

}
