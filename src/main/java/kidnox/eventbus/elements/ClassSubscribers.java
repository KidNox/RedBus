package kidnox.eventbus.elements;

import kidnox.eventbus.EventDispatcher;

import java.lang.reflect.Method;
import java.util.Map;

public final class ClassSubscribers {

    public static final ClassSubscribers EMPTY = new ClassSubscribers(null, null);

    public static boolean isNullOrEmpty(ClassSubscribers classSubscribers) {
        return classSubscribers == null || classSubscribers == EMPTY;
    }

    public final EventDispatcher eventDispatcher;
    public final Map<Class, Method> typedMethodsMap;

    public ClassSubscribers(EventDispatcher dispatcher, Map<Class, Method> typedMethodsMap) {
        this.eventDispatcher = dispatcher;
        this.typedMethodsMap = typedMethodsMap;
    }

    public int size() {
        return typedMethodsMap.size();
    }

}
