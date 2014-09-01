package kidnox.eventbus.internal.element;

import kidnox.eventbus.Dispatcher;

public final class ServiceElement extends Element {

    public final Dispatcher dispatcher;

    public ServiceElement(ElementInfo elementInfo, Object target, Dispatcher dispatcher) {
        super(elementInfo, target);
        this.dispatcher = dispatcher;
    }
}
