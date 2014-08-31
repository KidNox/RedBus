package kidnox.eventbus.test;

import kidnox.eventbus.util.EventTypeInterceptor;

public class EventTypeCountInterceptor extends EventTypeInterceptor {

    volatile int count;

    public EventTypeCountInterceptor(Class... classes) {
        super(classes);
    }

    @Override public boolean intercept(Object event) {
        count++;
        return super.intercept(event);
    }

    public int getCallCount() {
        return count;
    }
}
