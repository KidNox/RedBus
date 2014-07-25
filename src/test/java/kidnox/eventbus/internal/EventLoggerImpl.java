package kidnox.eventbus.internal;

import kidnox.eventbus.Element;
import kidnox.eventbus.EventLogger;

import java.util.Set;

public class EventLoggerImpl implements EventLogger {

    Object event;
    Set<? extends Element> elementSet;

    @Override public void logEvent(Object event, Set<? extends Element> elementSet) {
        this.event = event;
        this.elementSet = elementSet;
    }

    public Object getEvent() {
        return event;
    }

    public Set<? extends Element> getElementSet() {
        return elementSet;
    }
}
