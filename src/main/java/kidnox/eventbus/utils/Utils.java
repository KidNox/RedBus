package kidnox.eventbus.utils;

import java.lang.reflect.Method;
import java.util.Collection;

public final class Utils {

    public static <T> T checkNotNull(T instance) {
        if(instance == null) throw new NullPointerException();
        return instance;
    }

    public static boolean isNullOrEmpty(Object[] args) {
        return args == null || args.length == 0;
    }

    public static boolean notEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }

    public static Object invokeMethod(Object target, Method method, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    //no instance
    private Utils() {}
}
