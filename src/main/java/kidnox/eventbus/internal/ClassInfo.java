package kidnox.eventbus.internal;

import kidnox.eventbus.internal.element.ElementInfo;

import java.util.Map;

public final class ClassInfo {
    public final Class clazz;
    public final ClassType type;
    public final String annotationValue;
    public final Map<Class, ElementInfo> elements;

    public ClassInfo(Class clazz) {
        this(clazz, ClassType.NONE, null, null);
    }

    public ClassInfo(Class clazz, ClassType type, String annotationValue, Map<Class, ElementInfo> elements) {
        this.clazz = clazz;
        this.type = type;
        this.annotationValue = annotationValue;
        this.elements = elements;
    }

    public boolean isEmpty() {
        return elements == null || elements.isEmpty();
    }

    @Override public String toString() {
        return "ClassInfo{" +
                "clazz=" + clazz +
                ", type=" + type +
                ", annotationValue='" + annotationValue +
                '}';
    }
}
