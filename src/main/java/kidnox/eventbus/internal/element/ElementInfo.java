package kidnox.eventbus.internal.element;

import java.lang.reflect.Method;

public class ElementInfo {

    public final ElementType elementType;
    public final Class eventType;
    public final Method method;

    public ElementInfo(ElementType elementType, Class eventType, Method method) {
        this.elementType = elementType;
        this.eventType = eventType;
        this.method = method;
        method.setAccessible(true);
    }

    @Override public String toString() {
        return "ElementInfo{" +
                "elementType=" + elementType +
                ", eventType=" + eventType.getSimpleName() +
                ", method=" + method.getName() +
                '}';
    }
}
