package kidnox.eventbus;

public interface EventLogger {//TODO figure out about element encapsulation
    void logEvent(Object event, Object target, String what);
}
