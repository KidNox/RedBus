package kidnox.eventbus;

public interface DeadEventHandler {

    void onDeadEvent(Bus bus, Object event);

}
