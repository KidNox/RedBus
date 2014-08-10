package kidnox.eventbus.internal;

import kidnox.eventbus.Interceptor;

import java.util.HashSet;
import java.util.Set;

public class EventInterceptor implements Interceptor {

    Set<Class> set = new HashSet<Class>();

    @Override public boolean intercept(Object event) {
        return set.contains(event.getClass());
    }

    public void addIntercepted(Class clazz) {
        set.add(clazz);
    }
}
