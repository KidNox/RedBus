package kidnox.eventbus.test.exceptions;

import kidnox.eventbus.ExceptionHandler;

public class DefinedExceptionHandler implements ExceptionHandler {

    final boolean handle;

    private volatile Throwable throwable;
    private volatile Object target;
    private volatile Object event;

    private volatile int catchCount;

    public DefinedExceptionHandler(boolean handle) {
        this.handle = handle;
    }

    @Override public boolean handle(Throwable thr, Object target, Object event) {
        catchCount++;
        this.throwable = thr;
        this.target = target;
        this.event = event;
        return handle;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Object getTarget() {
        return target;
    }

    public Object getEvent() {
        return event;
    }

    public int getCatchCount() {
        return catchCount;
    }
}
