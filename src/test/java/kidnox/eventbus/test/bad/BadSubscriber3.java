package kidnox.eventbus.test.bad;

import kidnox.eventbus.Subscriber;

@Subscriber
public class BadSubscriber3 extends BadClass {
    public BadSubscriber3(String cause) {
        super(cause);
    }
}
