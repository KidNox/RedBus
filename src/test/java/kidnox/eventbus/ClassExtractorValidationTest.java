package kidnox.eventbus;

import kidnox.eventbus.internal.extraction.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;
import kidnox.eventbus.test.Event;
import kidnox.eventbus.test.Event2;
import kidnox.eventbus.test.bad.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ClassExtractorValidationTest {

    @SuppressWarnings("unchecked") @Parameterized.Parameters
    public static Collection testTargets() {
        return Arrays.asList(new Object[][]{
                {new NotVoidReturnTypeSubscriber()},
                {new WrongMethodArgsNumberSubscriber()},
                {new WrongMethodArgsNumber2Subscriber()},
                {new InterfaceEventSubscriber()},
                {new VoidReturnTypeProducer()},
                {new WrongMethodArgsNumberProducer()},
                {new InterfaceEventProducer()},
                {new SameEventsSubscriber()},
                {new SameEventsProducer()},
                {new SameEventsHandler()},
                {new SameEventsSubscriberHandler()},
                {new WrongArgsEventHandler()},
                {new WrongReturnTypeHandler()},
                {new WrongExecuteMethodTask()},
                {new ToManyExecuteMethodsTask()},
                {new WrongInheritDispatcherSubscriber()},
                {new WrongInheritDispatcherProducer()}});
    }

    final BadClass testInstance;
    final ClassInfoExtractor classInfoExtractor;

    public ClassExtractorValidationTest(BadClass testInstance) {
        this.testInstance = testInstance;
        this.classInfoExtractor = InternalFactory.createClassInfoExtractor();
    }

    @Test public void validationTest() {
        try {
            classInfoExtractor.getClassInfo(testInstance.getClass());
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

    @Subscriber
    private static class SameEventsSubscriber extends BadClass {
        public SameEventsSubscriber() {
            super("to many subscribers for event");
        }

        @Subscribe public void obtainEvent1(Event event) {}

        @Subscribe public void obtainEvent2(Event event) {}
    }

    @Producer
    private static class SameEventsProducer extends BadClass {
        public SameEventsProducer() {
            super("to many producers for event");
        }

        @Produce public Event produceEvent1() {
            return new Event();
        }

        @Produce public Event produceEvent2() {
            return new Event();
        }
    }

    @Subscriber
    private static class SameEventsHandler extends BadClass {
        public SameEventsHandler() {
            super("to many event handlers for event");
        }

        @Handle public Event2 handleEvent(Event event) {
            return null;
        }

        @Handle public Object handleEvent2(Event event) {
            return null;
        }
    }

    @Subscriber
    private static class SameEventsSubscriberHandler extends BadClass {
        public SameEventsSubscriberHandler() {
            super("same event for subscriber and event handler");
        }

        @Handle public Event2 handleEvent(Event event) {
            return null;
        }

        @Subscribe public void obtainEvent(Event event) { }
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

    @Subscriber
    private static class WrongArgsEventHandler extends BadClass {
        public WrongArgsEventHandler() {
            super("method annotated with @Handle must require a single element.");
        }

        @Handle public Event handleEvent() {
            return null;
        }
    }

    @Subscriber
    private static class WrongReturnTypeHandler extends BadClass {
        public WrongReturnTypeHandler() {
            super("method annotated with @Handle can't be void.");
        }

        @Handle public void handleEvent(Event event) {}
    }

    @Task
    private static class WrongExecuteMethodTask extends BadClass {
        public WrongExecuteMethodTask() {
            super("method annotated with @Execute must require zero arguments");
        }

        @Execute public void execute(Object param) {}
    }

    @Task
    private static class ToManyExecuteMethodsTask extends BadClass {
        public ToManyExecuteMethodsTask() {
            super("task can contain only one @Execute method.");
        }

        @Execute public void execute() {}

        @Execute public void execute2() {}
    }

    @Subscriber("wrong")
    private static class WrongInheritDispatcherSubscriber extends BadSubscriber3 {
        public WrongInheritDispatcherSubscriber() {
            super("bad dispatcher");
        }
    }

    @Producer("wrong")
    private static class WrongInheritDispatcherProducer extends BadProducer3 {
        public WrongInheritDispatcherProducer() {
            super("bad dispatcher");
        }
    }

}
