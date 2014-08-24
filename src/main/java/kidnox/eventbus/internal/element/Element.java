package kidnox.eventbus.internal.element;

import java.lang.reflect.InvocationTargetException;

public class Element {

    public final Class eventType;
    public final ElementInfo elementInfo;
    public final Object target;

    protected Element(ElementInfo elementInfo, Object target) {
        this.elementInfo = elementInfo;
        this.eventType = elementInfo.eventType;
        this.target = target;
        elementInfo.method.setAccessible(true);
    }

    public Object invoke(Object... args) throws InvocationTargetException {
        try {
            return elementInfo.method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public String toString() {
        return getClass().getSimpleName()+"{" +
                "elementInfo=" + elementInfo +
                ", targetCLass=" + target.getClass().getSimpleName() +
                '}';
    }

}
