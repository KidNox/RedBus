package kidnox.eventbus;

public interface EventLogger {
    void logEvent(Object event, Object target, String what);
}
