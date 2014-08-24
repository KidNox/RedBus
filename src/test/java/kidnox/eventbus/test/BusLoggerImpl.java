package kidnox.eventbus.test;

import kidnox.eventbus.BusLogger;

public class BusLoggerImpl implements BusLogger {

    Object event;
    Object element;
    String what;

    @Override public void logEvent(Object event, Object target, String what) {
        this.event = event;
        this.element = target;
        this.what = what;
    }

    public Object getEvent() {
        return event;
    }

    public Object getTarget() {
        return element;
    }

    public String getWhat() {
        return what;
    }
}
