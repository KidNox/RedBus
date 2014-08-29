package kidnox.eventbus.test.bad;

import kidnox.eventbus.Producer;

@Producer
public class BadProducer3 extends BadClass {
    public BadProducer3(String cause) {
        super(cause);
    }
}
