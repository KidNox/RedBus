package kidnox.eventbus.internal;

import java.lang.reflect.Method;

public final class ElementInfo {
    public final ElementType elementType;
    public final Class eventType;
    public final Method method;

    public ElementInfo(ElementType elementType, Class eventType, Method method) {
        this.elementType = elementType;
        this.eventType = eventType;
        this.method = method;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementInfo that = (ElementInfo) o;

        return elementType == that.elementType && eventType.equals(that.eventType);
    }

    @Override public int hashCode() {
        int result = elementType.hashCode();
        result = 31 * result + eventType.hashCode();
        return result;
    }

    @Override public String toString() {
        return "ElementInfo{" +
                "elementType=" + elementType +
                ", eventType=" + eventType.getSimpleName() +
                ", method=" + method.getName() +
                '}';
    }
}
