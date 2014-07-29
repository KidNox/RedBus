package kidnox.eventbus.internal.bad;

import kidnox.eventbus.Subscribe;

import static org.junit.Assert.fail;

public class BadSubscriber {

    @Subscribe public void notSubscribedMethod(Object event) {
        fail("class not annotated with @Subscriber!");
    }

}
