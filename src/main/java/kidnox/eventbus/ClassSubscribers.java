package kidnox.eventbus;

import java.lang.reflect.Method;
import java.util.Map;

public final class ClassSubscribers {

    public static final ClassSubscribers EMPTY = new ClassSubscribers(null, null);

    public static boolean isNullOrEmpty(ClassSubscribers classSubscribers) {
        return classSubscribers == null || classSubscribers == EMPTY;
    }

    public final Dispatcher dispatcher;
    public final Map<Class, Method> typedMethodsMap;

    public ClassSubscribers(Dispatcher dispatcher, Map<Class, Method> typedMethodsMap) {
        this.dispatcher = dispatcher;
        this.typedMethodsMap = typedMethodsMap;
    }

    public int size() {
        return typedMethodsMap.size();
    }

}
