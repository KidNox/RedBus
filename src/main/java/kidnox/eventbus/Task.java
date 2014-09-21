package kidnox.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can contain {@link OnRegister}, {@link OnUnregister}, and {@link Execute} elements.
 * After target object has been registered in Bus and OnRegister invoked (if defined),
 * called Execute method. When work is completed task unregister itself and OnUnregistered is invoked.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Task {

    /**
     * Name of the dispatcher for dispatchers factory
     * */
    String value() default "";
}
