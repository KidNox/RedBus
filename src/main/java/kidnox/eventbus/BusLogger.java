package kidnox.eventbus;

public interface BusLogger {//TODO figure out about element encapsulation
    void logEvent(Object event, Object target, String what);
}
