package kidnox.eventbus;

import kidnox.eventbus.async.AsyncDispatcherFactory;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.internal.bad.BadClass;
import kidnox.eventbus.internal.bad.BadProducer2;
import kidnox.eventbus.internal.bad.BadSubscriber2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ClassExtractorValidationTest {

    @SuppressWarnings("unchecked") @Parameterized.Parameters
    public static Collection classExtractorFactories() {
        return Arrays.asList(new Object[][]{
                {new BadSubscriber2()},
                {new BadProducer2()},
                {new NotVoidReturnTypeSubscriber()},
                {new WrongMethodArgsNumberSubscriber()},
                {new WrongMethodArgsNumber2Subscriber()},
                {new InterfaceEventSubscriber()},
                {new VoidReturnTypeProducer()},
                {new WrongMethodArgsNumberProducer()},
                {new InterfaceEventProducer()}});
    }

    final BadClass testInstance;
    final ClassInfoExtractor classInfoExtractor;

    public ClassExtractorValidationTest(BadClass testInstance) {
        this.testInstance = testInstance;
        this.classInfoExtractor = InternalFactory.createClassInfoExtractor(
                new AsyncDispatcherFactory(EventDispatcher.MAIN, EventDispatcher.WORKER), true);
    }

    @Test public void validationTest() {
        try {
            classInfoExtractor.getTypeOf(testInstance.getClass());
            fail(testInstance.cause);
        } catch (RuntimeException ignored) {
            //ignored.printStackTrace();
        }
    }


    @Subscriber
    private static class NotVoidReturnTypeSubscriber extends BadClass {
        public NotVoidReturnTypeSubscriber() {
            super("method annotated with @Subscribe must be void.");
        }

        @Subscribe public Object obtainEvent(Object event) {
            return event;
        }
    }

    @Subscriber
    private static class WrongMethodArgsNumberSubscriber extends BadClass {
        public WrongMethodArgsNumberSubscriber() {
            super("method annotated with @Subscribe must require a single argument.");
        }

        @Subscribe public void obtainEvent() {}
    }

    @Subscriber
    private static class WrongMethodArgsNumber2Subscriber extends BadClass {
        public WrongMethodArgsNumber2Subscriber() {
            super("method annotated with @Subscribe must require a single argument.");
        }

        @Subscribe public void obtainEvent(Object event, Object event2) {}
    }

    @Subscriber
    private static class InterfaceEventSubscriber extends BadClass {
        public InterfaceEventSubscriber() {
            super("method annotated with @Subscribe can't subscribe for interface.");
        }

        @Subscribe public void obtainEvent(Runnable event) {}
    }


    @Producer
    private static class VoidReturnTypeProducer extends BadClass {
        public VoidReturnTypeProducer() {
            super("method annotated with @Produce can't be void.");
        }

        @Produce public void produceEvent() {}
    }

    @Producer
    private static class WrongMethodArgsNumberProducer extends BadClass {
        public WrongMethodArgsNumberProducer() {
            super("method annotated with @Produce must require zero arguments.");
        }

        @Produce public Object produceEvent(Object event) {
            return event;
        }
    }

    @Producer
    private static class InterfaceEventProducer extends BadClass {
        public InterfaceEventProducer() {
            super("method annotated with @Produce can't produce interface.");
        }

        @Produce public Runnable produceEvent() {
            return null;
        }
    }


}
