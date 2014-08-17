package kidnox.eventbus.elements;

import kidnox.eventbus.ExceptionHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractElement {

    public final Class eventClass;
    public final Object target;
    public final Method method;

    final ExceptionHandler exceptionHandler;

    protected AbstractElement(Class eventClass, Object target, Method method, ExceptionHandler exceptionHandler) {
        this.eventClass = eventClass;
        this.target = target;
        this.method = method;
        this.exceptionHandler = exceptionHandler;
        method.setAccessible(true);
    }

    public abstract Object invoke(Object event);

    protected Object invokeMethod(Object... args) {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            if(exceptionHandler != null &&
                    exceptionHandler.handle(e.getCause(), target, args.length == 0 ? null : args[0])) {
                return null;
            } else {
                throw new RuntimeException(e.getCause());
            }
        } catch (ReflectiveOperationException e) {
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
