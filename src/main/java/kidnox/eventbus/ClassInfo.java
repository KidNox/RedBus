package kidnox.eventbus;

import kidnox.common.Pair;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public final class ClassInfo {

    public static final ClassInfo EMPTY = new ClassInfo(null);

    public static boolean isNullOrEmpty(ClassInfo classInfo) {
        return classInfo == null || classInfo == EMPTY;
    }

    public final List<Pair<Dispatcher, Map<Class, Method>>> dispatchersToTypedMethodList;

    public ClassInfo(List<Pair<Dispatcher, Map<Class, Method>>> dispatchersToTypedMethodMap) {
        this.dispatchersToTypedMethodList = dispatchersToTypedMethodMap;
    }

}
