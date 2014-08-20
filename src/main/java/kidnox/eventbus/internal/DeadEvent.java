package kidnox.eventbus.internal;

public final class DeadEvent {
    public final Object actual;

    public DeadEvent(Object actual) {
        this.actual = actual;
    }
}
