package kidnox.eventbus;

import java.lang.reflect.Method;

public abstract class Element {

    public final Class eventClass;
    public final Object target;
    public final Method method;
    public final int hashCode;

    protected Element(Class eventClass, Object target, Method method) {
        this.eventClass = eventClass;
        this.target = target;
        this.method = method;
        method.setAccessible(true);
        // from otto
        // Compute hash code eagerly since we know it will be used frequently and we cannot estimate the runtime of the
        // target's hashCode call.
        hashCode = (31 + method.hashCode()) * 31 + target.hashCode();
    }

    protected abstract Object invoke(Object event);

    @Override public String toString() {
        return getClass().getSimpleName()+"{" +
                "eventClass=" + eventClass +
                ", target=" + target +
                ", method=" + method +
                '}';
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof Element) {
            Element that = (Element) obj;
            // from guava
            // Use == so that different equal instances will still receive events.
            // We only guard against the case that the same object is registered
            // multiple times
            return target == that.target && method.equals(that.method);
        }
        return false;
    }
}
