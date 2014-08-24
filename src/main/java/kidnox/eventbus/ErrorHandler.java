package kidnox.eventbus;

public interface ErrorHandler {

    boolean handle(Throwable thr, Object target, Object event);

}
