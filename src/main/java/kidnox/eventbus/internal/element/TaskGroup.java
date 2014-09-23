package kidnox.eventbus.internal.element;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.AsyncBus;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ElementsGroup;
import kidnox.eventbus.internal.Utils;

public final class TaskGroup extends ElementsGroup {

    public TaskGroup(ClassInfo classInfo, Dispatcher dispatcher) {
        super(classInfo, dispatcher);
    }

    @Override public void registerGroup(final Object target, final AsyncBus bus) {
        if(dispatcher.isDispatcherThread()) {
            executeTask(target, bus);
        } else {
            dispatcher.dispatch(new Runnable() {
                @Override public void run() {
                    executeTask(target, bus);
                }
            });
        }
    }

    private void executeTask(Object target, AsyncBus bus) {
        ListenerInfo listenerInfo = (ListenerInfo) classInfo.elements.get(Utils.REGISTER_KEY);
        if(listenerInfo != null)
            invokeElement(new Element(listenerInfo, target), bus, listenerInfo.withBusArgument ? bus : null);

        ElementInfo elementInfo = classInfo.elements.get(Utils.EXECUTE_KEY);
        if(elementInfo != null) {
            Object event = invokeElement(new Element(elementInfo, target), bus, null);
            if(event != null) bus.post(event);
        }

        bus.unregister(target);
        listenerInfo = (ListenerInfo) classInfo.elements.get(Utils.UNREGISTER_VOID_KEY);
        if(listenerInfo != null)
            dispatch(new Element(listenerInfo, target), bus, listenerInfo.withBusArgument ? bus : null);
    }

    @Override public void unregisterGroup(Object target, AsyncBus bus) { }

}
