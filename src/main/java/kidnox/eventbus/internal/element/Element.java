package kidnox.eventbus.internal.element;

import java.lang.reflect.InvocationTargetException;

public class Element {

    public final ElementInfo elementInfo;
    public final Object target;

    public Element(ElementInfo elementInfo, Object target) {
        this.elementInfo = elementInfo;
        this.target = target;
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
