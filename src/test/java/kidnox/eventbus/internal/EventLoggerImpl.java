package kidnox.eventbus.internal;

import kidnox.eventbus.EventLogger;

public class EventLoggerImpl implements EventLogger {

    Object event;
    Object element;
    String what;

    @Override public void logEvent(Object event, Object element, String what) {
        this.event = event;
        this.element = element;
        this.what = what;
    }

    public Object getEvent() {
        return event;
    }

    public Object getElement() {
        return element;
    }

    public String getWhat() {
        return what;
    }
}
