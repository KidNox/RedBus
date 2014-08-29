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

    public static void throwBusException(String action, Object cause, String message) {//TODO move to bus?
        throw new BusException(action + " was failed " + cause + message);
    }

/*    public static <K, V> MapBuilder<K, V> unmodifiableMap(int size) {
        return new MapBuilder<K, V>(new HashMap<K, V>(size));
    }

    public static class MapBuilder<K, V> {
        final Map<K, V> tempMap;

        MapBuilder(Map<K, V> tempMap) {
            this.tempMap = tempMap;
        }

        public MapBuilder<K, V> with(K key, V value) {
            tempMap.put(key, value);
            return this;
        }

        public Map<K, V> map() {
            return Collections.unmodifiableMap(tempMap);
        }
    }*/

    //no instance
    private Utils() {}
}
