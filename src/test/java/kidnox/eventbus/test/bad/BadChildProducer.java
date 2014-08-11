package kidnox.eventbus.test.bad;

import kidnox.eventbus.Produce;
import kidnox.eventbus.test.SimpleProducer;

import static org.junit.Assert.fail;

public class BadChildProducer extends SimpleProducer {

    @Produce public String produceString() {
        fail("class not annotated with @Producer!");
        return null;
    }

}
