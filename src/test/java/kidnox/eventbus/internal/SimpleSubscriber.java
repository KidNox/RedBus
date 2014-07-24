package kidnox.eventbus.internal;

import kidnox.eventbus.Subscribe;
import kidnox.eventbus.Subscriber;

import static org.junit.Assert.fail;

@Subscriber
public class SimpleSubscriber {

    private Object currentEvent;

    @Subscribe public void publicObtainString(Object event) {
        currentEvent = event;
    }

    @Subscribe protected void protectedObtainString(Object event) {
        fail("must not call protected methods");
    }

    @Subscribe private void privateObtainString(Object event) {
        fail("must not call private methods");
    }

    @Subscribe void packageObtainString(Object event) {
        fail("must not call package-local methods");
    }

    public void notSubscribedMethod(Object event) {
        fail("must not call methods without @Subscribe!");
    }

    public Object getCurrentEvent() {
        return currentEvent;
    }
}
