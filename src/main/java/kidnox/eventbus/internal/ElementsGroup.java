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
                dispatch(new Element(classInfo.onRegisterListener, target), bus, bus);
            else
                dispatch(new Element(classInfo.onRegisterListener, target), bus, null);
        }
    }

    public void unregisterGroup(Object target, AsyncBus bus) {
        if(classInfo.onUnRegisterListener != null) {
            if(classInfo.onUnRegisterListener.eventType == Utils.UNREGISTER_BUS_KEY)
                dispatch(new Element(classInfo.onUnRegisterListener, target), bus, bus);
            else
                dispatch(new Element(classInfo.onUnRegisterListener, target), bus, null);
        }
    }

    protected void dispatch(final Element element, final AsyncBus bus, final Object param) {
        if(dispatcher.isDispatcherThread()) {
            invokeElement(element, bus, param);
        } else {
            dispatcher.dispatch(new Runnable() {
                @Override public void run() {
                    invokeElement(element, bus, param);
                }
            });
        }
    }

    protected Object invokeElement(Element element, AsyncBus bus, Object param) {
        if(param == null)   return bus.invokeElement(element);
        else                return bus.invokeElement(element, param);
    }

    static final ElementsGroup EMPTY = new ElementsGroup(null, null) {
        @Override public void registerGroup(Object target, AsyncBus asyncBus) { }

        @Override public void unregisterGroup(Object target, AsyncBus bus) { }
    };
}
