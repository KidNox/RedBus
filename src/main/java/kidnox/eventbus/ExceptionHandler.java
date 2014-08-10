package kidnox.eventbus;

public interface ExceptionHandler {

    boolean handle(Throwable thr, Object target, Object event);

}
