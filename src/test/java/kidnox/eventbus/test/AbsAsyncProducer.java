package kidnox.eventbus.test;

public class AbsAsyncProducer {
    protected volatile int producedCount = 0;
    protected volatile Object lastEvent;

    public int getProducedCount() {
        return producedCount;
    }

    public Object getLastEvent() {
        return lastEvent;
    }
}
