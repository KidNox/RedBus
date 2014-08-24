package kidnox.eventbus.util;

import kidnox.eventbus.EventInterceptor;

import java.util.HashSet;
import java.util.Set;

public class EventTypeInterceptor implements EventInterceptor {

    final Set<Class> set = new HashSet<Class>();

    public EventTypeInterceptor(Class... classes) {
        for (Class clazz : classes) {
            addIntercepted(clazz);
        }
    }

    @Override public boolean intercept(Object event) {
        return set.contains(event.getClass());
    }

    /** Not thread safe */
    public void addIntercepted(Class clazz) {
        set.add(clazz);
    }
}
