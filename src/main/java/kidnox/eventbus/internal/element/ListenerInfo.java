package kidnox.eventbus.internal.element;

import java.lang.reflect.Method;

/**ElementInfo for @OnRegister and @OnUnregister methods*/
public final class ListenerInfo extends ElementInfo {

    public final boolean withBusArgument; //for optimization, on android Method#getParametersTypes return cloned array

    public ListenerInfo(ElementType elementType, Class eventType, Method method, boolean withBusArgument) {
        super(elementType, eventType, method);
        this.withBusArgument = withBusArgument;
    }

}
