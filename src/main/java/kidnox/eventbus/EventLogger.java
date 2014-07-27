package kidnox.eventbus;

public interface EventLogger {
    void logEvent(Object event, Object element, String what);
}
