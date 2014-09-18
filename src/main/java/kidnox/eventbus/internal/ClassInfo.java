package kidnox.eventbus.internal;

import kidnox.eventbus.internal.element.ElementInfo;

import java.util.Collection;

public final class ClassInfo {
    public final Class clazz;
    public final ClassType type;
    public final String annotationValue;
    public final Collection<ElementInfo> elements;
    //TODO maybe better to keep listeners in element collection (use map optionally)
    public final ElementInfo onRegisterListener;
    public final ElementInfo onUnRegisterListener;

    public ClassInfo(Class clazz) {
        this(clazz, ClassType.NONE, null, null);
    }

    public ClassInfo(Class clazz, ClassType type, String annotationValue, Collection<ElementInfo> elements) {
        this(clazz, type, annotationValue, elements, null, null);
    }

    public ClassInfo(Class clazz, ClassType type, String annotationValue, Collection<ElementInfo> elements,
                     ElementInfo onRegisterListener, ElementInfo onUnRegisterListener) {
        this.clazz = clazz;
        this.type = type;
        this.annotationValue = annotationValue;
        this.elements = elements;
        this.onRegisterListener = onRegisterListener;
        this.onUnRegisterListener = onUnRegisterListener;
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
