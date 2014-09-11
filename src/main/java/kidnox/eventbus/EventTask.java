package kidnox.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can contain {@link OnRegister}, {@link OnUnregister}, and one of {@link Execute} or {@link Schedule} elements.
 * After target object has been registered in Bus and OnRegister invoked (if defined),
 * called Execute or Schedule method. When work is completed task unregister itself and OnUnregistered is invoked.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventTask {

    /**
     * Name of the dispatcher for dispatchers factory
     * */
    String value() default "";
}
