package kidnox.eventbus.util;

import kidnox.eventbus.EventInterceptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EventTypeInterceptor implements EventInterceptor {

    final Set<Class> set = new HashSet<Class>();

    public EventTypeInterceptor(Class... classes) {
        Collections.addAll(set, classes);
    }

    @Override public boolean intercept(Object event) {
        return set.contains(event.getClass());
    }

}
