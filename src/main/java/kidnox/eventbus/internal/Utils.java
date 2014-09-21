package kidnox.eventbus.internal;

import java.util.*;

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

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <K, V> HashMap<K, V> newHashMap(int capacity) {
        return new HashMap<K, V>(capacity);
    }

    public static <T> HashSet<T> newHashSet() {
        return new HashSet<T>();
    }

    public static <T> HashSet<T> newHashSet(int capacity) {
        return new HashSet<T>(capacity);
    }

    public static void throwBusException(String action, Object cause, String message) {
        throw new BusException(action + " was failed " + cause + message);
    }

    public static final Class REGISTER_VOID_KEY = OnRegisterVoidType.class;
    public static final Class REGISTER_BUS_KEY = OnRegisterBusType.class;
    public static final Class UNREGISTER_VOID_KEY = OnUnregisterVoidType.class;
    public static final Class UNREGISTER_BUS_KEY = OnUnregisterBusType.class;
    public static final Class EXECUTE_KEY = ExecuteType.class;

    private static class OnRegisterVoidType {}
    private static class OnRegisterBusType {}
    private static class OnUnregisterVoidType {}
    private static class OnUnregisterBusType {}
    private static class ExecuteType {}

    private Utils() {}
}
