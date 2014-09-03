package kidnox.eventbus.internal.element;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ElementsGroup;

public final class TaskGroup extends ElementsGroup {

    public TaskGroup(ClassInfo classInfo, Dispatcher dispatcher) {
        super(classInfo, dispatcher);
    }

    @Override public void registerGroup(Object target, AsyncBus bus) {

    }

    @Override public void unregisterGroup(AsyncBus bus) {

    }
}
