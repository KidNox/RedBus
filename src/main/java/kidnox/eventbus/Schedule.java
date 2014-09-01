package kidnox.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Schedule {//TODO figure out how to cancel this task

    long delay() default 0;

    long period();

    TimeUnit timeUnit();

    /**infinite when -1*/
    int times() default -1;

}
