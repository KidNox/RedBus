package kidnox.eventbus;

import kidnox.eventbus.internal.Event;
import kidnox.eventbus.internal.Event2;
import kidnox.eventbus.internal.EventInterceptor;
import kidnox.eventbus.internal.EventsSubscriber;
import org.junit.Test;

import static org.junit.Assert.*;

public class InterceptorTest {

    Bus bus;

    @Test public void interceptorTest() {
        EventInterceptor interceptor = new EventInterceptor();
        interceptor.addIntercepted(Event.class);
        bus = Bus.Factory.builder().withInterceptor(interceptor).create();

        EventsSubscriber eventsSubscriber = new EventsSubscriber();
        bus.register(eventsSubscriber);

        bus.post(new Event());
        bus.post(new Event2());

        assertNull(eventsSubscriber.getEvent());
        assertNotNull(eventsSubscriber.getEvent2());
    }

}
