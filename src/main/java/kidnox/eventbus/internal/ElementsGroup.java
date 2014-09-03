package kidnox.eventbus.internal;

import kidnox.eventbus.Dispatcher;

public abstract class ElementsGroup {

    protected final ClassInfo classInfo;
    protected final Dispatcher dispatcher;

    protected ElementsGroup(ClassInfo classInfo, Dispatcher dispatcher) {
        this.classInfo = classInfo;
        this.dispatcher = dispatcher;
    }

    public abstract void registerGroup(Object target, AsyncBus bus);

    public abstract void unregisterGroup(AsyncBus bus);


    static final ElementsGroup EMPTY = new ElementsGroup(null, null) {
        @Override public void registerGroup(Object target, AsyncBus asyncBus) { }

        @Override public void unregisterGroup(AsyncBus bus) { }
    };
}
