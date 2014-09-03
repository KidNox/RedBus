package kidnox.eventbus.internal.element;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ElementsGroup;

import java.util.Map;
import java.util.Set;

public final class ServiceGroup extends ElementsGroup {

    public ServiceGroup(ClassInfo classInfo, Dispatcher dispatcher) {
        super(classInfo, dispatcher);
    }

    @Override public void registerGroup(Object target, AsyncBus bus) {
        super.registerGroup(target, bus);
    }

    @Override public void unregisterGroup(Object target, AsyncBus bus) {
        super.unregisterGroup(target, bus);
    }
}
