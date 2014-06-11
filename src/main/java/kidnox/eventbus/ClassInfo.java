package kidnox.eventbus;

import java.lang.reflect.Method;
import java.util.Map;

public final class ClassInfo {

    public static final ClassInfo EMPTY = new ClassInfo(null);

    final Map<Dispatcher, Map<Class, Method>> dispatchersToTypedMethodMap;

    public ClassInfo(Map<Dispatcher, Map<Class, Method>> dispatchersToTypedMethodMap) {
        this.dispatchersToTypedMethodMap = dispatchersToTypedMethodMap;
    }

}
