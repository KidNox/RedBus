package kidnox.eventbus.test;

public class AbsAsyncSubscriber {
    protected volatile Object currentEvent;

    public Object getCurrentEvent() {
        return currentEvent;
    }
}
