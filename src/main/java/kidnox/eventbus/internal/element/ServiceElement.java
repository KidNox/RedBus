package kidnox.eventbus.internal.element;

import kidnox.eventbus.EventDispatcher;

public final class ServiceElement extends Element {

    public final EventDispatcher eventDispatcher;

    public ServiceElement(ElementInfo elementInfo, Object target, EventDispatcher eventDispatcher) {
        super(elementInfo, target);
        this.eventDispatcher = eventDispatcher;
    }
}
