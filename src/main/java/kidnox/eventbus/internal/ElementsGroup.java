package kidnox.eventbus.internal;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.element.Element;

public class ElementsGroup {

    protected final ClassInfo classInfo;
    protected final Dispatcher dispatcher;

    protected ElementsGroup(ClassInfo classInfo, Dispatcher dispatcher) {
        this.classInfo = classInfo;
        this.dispatcher = dispatcher;
    }

    public void registerGroup(Object target, AsyncBus bus) {
        if(classInfo.onRegisterListener != null) {
            if(classInfo.onRegisterListener.eventType == Utils.REGISTER_BUS_KEY)
                bus.invokeElement(new Element(classInfo.onRegisterListener, target), bus);
            else
                bus.invokeElement(new Element(classInfo.onRegisterListener, target));
        }
    }

    public void unregisterGroup(Object target, AsyncBus bus) {
        if(classInfo.onUnRegisterListener != null) {
            bus.invokeElement(new Element(classInfo.onUnRegisterListener, target));
        }
    }

    static final ElementsGroup EMPTY = new ElementsGroup(null, null) {
        @Override public void registerGroup(Object target, AsyncBus asyncBus) { }

        @Override public void unregisterGroup(Object target, AsyncBus bus) { }
    };
}
