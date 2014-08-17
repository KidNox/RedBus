package kidnox.eventbus.internal;

import java.lang.reflect.Method;
import java.util.Map;

public final class ClassInfo {
    public final Class clazz;
    public final ClassType type;
    public final String annotationValue;
    public final Map<Class, Method> typedMethodsMap;

    public ClassInfo(Class clazz) {
        this(clazz, ClassType.NONE, null, null);
    }

    public ClassInfo(Class clazz, ClassType type, String annotationValue, Map<Class, Method> typedMethodsMap) {
        this.clazz = clazz;
        this.type = type;
        this.annotationValue = annotationValue;
        this.typedMethodsMap = typedMethodsMap;
    }

    public boolean isEmpty() {
        return typedMethodsMap == null || typedMethodsMap.isEmpty();
    }

    @Override public String toString() {
        return "ClassInfo{" +
                "clazz=" + clazz +
                ", type=" + type +
                ", annotationValue='" + annotationValue + '\'' +
                '}';
    }
}
