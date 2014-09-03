package kidnox.eventbus.test.bad;

import kidnox.eventbus.Handle;
import kidnox.eventbus.Subscribe;
import kidnox.eventbus.Subscriber;
import kidnox.eventbus.test.Event;

@Subscriber
public class BadEventHandler {

    @Subscribe public void obtainEvent(Event e) {}

    @Handle public Object handleEvent(Event e) {
        return e;
    }

}
