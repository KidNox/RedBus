package kidnox.eventbus.internal.element;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ElementsGroup;

import java.util.Map;
import java.util.Set;

public final class ServiceGroup implements ElementsGroup {

    //final Map<ClassInfo, Object> instances;
    final ClassInfo classInfo;
    final Dispatcher dispatcher;
    final Map<Class, Set<AsyncElement>> subscribersMapRef;
    final Map<Class, AsyncElement> producerMapRef;

    public ServiceGroup(ClassInfo classInfo, Dispatcher dispatcher,
                        Map<Class, Set<AsyncElement>> subscribersMapRef, Map<Class, AsyncElement> producerMapRef) {
        this.classInfo = classInfo;
        this.dispatcher = dispatcher;
        this.subscribersMapRef = subscribersMapRef;
        this.producerMapRef = producerMapRef;
    }

    @Override public void registerGroup(final Object target) {

    }

    @Override public void unregisterGroup() {

    }
}
