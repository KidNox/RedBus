package kidnox.eventbus;

import kidnox.annotations.Nonnull;

import java.lang.reflect.Method;

public abstract class Element {

    protected final Class eventClass;
    protected final Object target;
    protected final Method method;
    private final int hashCode;

    protected Element(@Nonnull Class eventClass, @Nonnull Object target, @Nonnull Method method) {
        this.eventClass = eventClass;
        this.target = target;
        this.method = method;
        method.setAccessible(true);

        // Compute hash code eagerly since we know it will be used frequently and we cannot estimate the runtime of the
        // target's hashCode call.
        hashCode = (31 + method.hashCode()) * 31 + target.hashCode();
    }

    @Override
    public String toString() {
        return "Element{" +
                "eventClass=" + eventClass +
                ", target=" + target +
                ", method=" + method +
                '}';
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Element) {
            Element that = (Element) obj;
            // Use == so that different equal instances will still receive events.
            // We only guard against the case that the same object is registered
            // multiple times
            return target == that.target && method.equals(that.method);
        }
        return false;
    }
}
