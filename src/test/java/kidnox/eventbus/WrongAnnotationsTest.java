package kidnox.eventbus;

import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.internal.extraction.ClassInfoExtractor;
import kidnox.eventbus.test.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class WrongAnnotationsTest {

    @SuppressWarnings("unchecked") @Parameterized.Parameters
    public static Collection testTargets() {
        return Arrays.asList(new Object[][]{
                {new SubscriberWithProduceMethods()},
                {new SubscriberWithExecuteMethods()},
                {new ProducerWithSubscribeMethod()},
                {new ProducerWithHandleMethod()},
                {new ProducerWithExecuteMethod()},
                {new TaskWithSubscribeMethod()},
                {new TaskWithHandleMethod()},
                {new TaskWithProduceMethod()},
        });
    }

    final ClassInfoExtractor extractor;
    final Object target;

    public WrongAnnotationsTest(Object target) {
        extractor = InternalFactory.createClassInfoExtractor();
        this.target = target;
    }

    @Test public void validationTest() {
        try {
            extractor.getClassInfo(target.getClass());
            fail("wrong annotation");
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }


    @Subscriber
    static class SubscriberWithProduceMethods {
        @Produce public Object produceEvent() {
            return null;
        }
    }

    @Subscriber
    static class SubscriberWithExecuteMethods {
        @Execute public void execute() { }
    }

    @Producer
    static class ProducerWithSubscribeMethod {
        @Subscribe public void obtainEvent(Object event) {}
    }

    @Producer
    static class ProducerWithHandleMethod {
        @Handle public Event handleEvent(Object object) {
            return null;
        }
    }

    @Producer
    static class ProducerWithExecuteMethod {
        @Execute public void execute() {}
    }

    @Task
    static class TaskWithSubscribeMethod {
        @Subscribe public void subscribe(Object event) {}
    }

    @Task
    static class TaskWithHandleMethod {
        @Handle public Event handle(Object event) {
            return null;
        }
    }

    @Task
    static class TaskWithProduceMethod {
        @Produce public Object produce() {
            return null;
        }
    }

}
