package kidnox.eventbus;

import java.lang.reflect.Method;
import java.util.Map;

public final class ClassProducers {

    public static final ClassProducers EMPTY = new ClassProducers(null);

    public static boolean isNullOrEmpty(ClassProducers classProducers) {
        return classProducers == null || classProducers == EMPTY;
    }

    public final Map<Class, Method> typedMethodsMap;

    public ClassProducers(Map<Class, Method> typedMethodsMap) {
        this.typedMethodsMap = typedMethodsMap;
    }
}
