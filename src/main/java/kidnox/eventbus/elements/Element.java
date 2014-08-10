package kidnox.eventbus.elements;

import java.lang.reflect.Method;

public abstract class Element {

    public final Class eventClass;
    public final Object target;
    public final Method method;

    protected Element(Class eventClass, Object target, Method method) {
        this.eventClass = eventClass;
        this.target = target;
        this.method = method;
        method.setAccessible(true);
    }

    public abstract Object invoke(Object event);

    @Override public String toString() {
        return getClass().getSimpleName()+"{" +
                ", targetClass=" + target.getClass().getSimpleName() +
                ", eventClass=" + eventClass.getSimpleName() +
                ", method=" + method.getName() +
                '}';
    }

}
