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
        if(classInfo.onRegisterListener != null) {
            if(classInfo.onRegisterListener.eventType == Utils.REGISTER_BUS_KEY)
                invokeElement(new Element(classInfo.onRegisterListener, target), bus, bus);
            else
                invokeElement(new Element(classInfo.onRegisterListener, target), bus, null);
        }
        for(ElementInfo entry : classInfo.elements) {
            Object event = invokeElement(new Element(entry, target), bus, null);
            if(event != null) bus.post(event);
        }
        bus.unregister(target);
        if(classInfo.onUnRegisterListener != null) {
            if(classInfo.onUnRegisterListener.eventType == Utils.UNREGISTER_BUS_KEY)
                invokeElement(new Element(classInfo.onUnRegisterListener, target), bus, bus);
            else
                invokeElement(new Element(classInfo.onUnRegisterListener, target), bus, null);
        }
    }

    @Override public void unregisterGroup(Object target, AsyncBus bus) { }

}
