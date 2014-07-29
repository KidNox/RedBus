package kidnox.eventbus.internal.bad;

import kidnox.eventbus.Produce;

import static org.junit.Assert.fail;

public class BadProducer {

    @Produce public Object produceObject() {
        fail("class not annotated with @Producer!");
        return null;
    }

}
