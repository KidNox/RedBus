package kidnox.eventbus.elements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Element {

    public final Class eventClass;
    public final Object target;
    public final Method method;

    protected Element(Class eventClass, Object target, Method method) {
        this.eventClass = eventClass;
        this.target = target;
        this.method = method;
        method.setAccessible(true);
    }

    public Object invoke(Object... args) throws InvocationTargetException {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public String toString() {
        return getClass().getSimpleName()+"{" +
                ", targetClass=" + target.getClass().getSimpleName() +
                ", eventClass=" + eventClass.getSimpleName() +
                ", method=" + method.getName() +
                '}';
    }

}
