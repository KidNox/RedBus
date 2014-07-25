package kidnox.eventbus;

import java.util.Set;

public interface EventLogger {
    void logEvent(Object event, Set<? extends Element> elementSet);
}
