package kidnox.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Processor {
    String DEFAULT = "";

    /**
     * Name of the dispatcher for dispatchers factory
     * */
    String value() default DEFAULT;
}
