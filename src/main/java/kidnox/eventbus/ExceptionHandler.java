package kidnox.eventbus;

public interface ExceptionHandler {

    boolean handle(Throwable throwable, Object target, Object event);

}
