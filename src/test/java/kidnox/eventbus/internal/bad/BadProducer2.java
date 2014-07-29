package kidnox.eventbus.internal.bad;

import kidnox.eventbus.Produce;
import kidnox.eventbus.Producer;

import static org.junit.Assert.fail;

@Producer
public class BadProducer2 extends BadClass {

    private Object currentEvent;

    public BadProducer2() {
        super("not public method annotated with @Produce");
    }

    @Produce public Object publicProduceEvent() {
        return new Object();
    }

    @Produce protected Object protectedProduceEvent() {
        fail("must not call protected methods");
        return null;
    }

    @Produce private Object privateProduceEvent() {
        fail("must not call private methods");
        return null;
    }

    @Produce Object packageProduceEvent() {
        fail("must not call package-local methods");
        return null;
    }

    public void notSubscribedMethod(Object event) {
        fail("must not call methods without @Produce!");
    }


}
