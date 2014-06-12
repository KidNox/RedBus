package kidnox.eventbus.internal;

public class AbsAsyncSubscriber {
    protected volatile Object currentEvent;

    public Object getCurrentEvent() {
        return currentEvent;
    }
}
