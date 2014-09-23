package kidnox.eventbus.internal;

import kidnox.eventbus.Execute;
import kidnox.eventbus.OnRegister;
import kidnox.eventbus.OnUnregister;

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

    public static final Class REGISTER_KEY = OnRegister.class;
    public static final Class UNREGISTER_VOID_KEY = OnUnregister.class;
    public static final Class EXECUTE_KEY = Execute.class;


    private Utils() {}
}
