package kidnox.eventbus.test.bad;

import kidnox.eventbus.Subscribe;
import kidnox.eventbus.Subscriber;

import static org.junit.Assert.fail;

@Subscriber
public class BadSubscriber2 extends BadClass {

    private Object currentEvent;

    public BadSubscriber2() {
        super("not public method annotated with @Subscribe");
    }

    @Subscribe public void publicObtainEvent(Object event) {
        currentEvent = event;
    }

    @Subscribe protected void protectedObtainEvent(Object event) {
        fail("must not call protected methods");
    }

    @Subscribe private void privateObtainEvent(Object event) {
        fail("must not call private methods");
    }

    @Subscribe void packageObtainEvent(Object event) {
        fail("must not call package-local methods");
    }

    public void notSubscribedMethod(Object event) {
        fail("must not call methods without @Subscribe!");
    }

    public Object getCurrentEvent() {
        return currentEvent;
    }
}
