package kidnox.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation for method with zero or one argument, in EventTask class.
 * When method require single argument, event of that type can be used for task cancellation.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Schedule {

    long delay() default 0;

    long period();

    TimeUnit timeUnit();

    /**infinite when -1*/
    int times() default -1;

}
