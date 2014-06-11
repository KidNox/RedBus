package kidnox.eventbus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Subscriber {

    /**
     * Name of the dispatcher for Dispatcher.Factory
     * */
    String value() default "";
}
