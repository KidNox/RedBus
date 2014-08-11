package kidnox.eventbus.test.bad;

import kidnox.eventbus.Subscribe;
import kidnox.eventbus.test.SimpleSubscriber;

import static org.junit.Assert.fail;

public class BadChildSubscriber extends SimpleSubscriber {

    @Subscribe public void obtainString(String s) {
        fail("class not annotated with @Subscriber!");
    }

}
