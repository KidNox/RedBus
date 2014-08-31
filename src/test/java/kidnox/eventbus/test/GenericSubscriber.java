package kidnox.eventbus.test;

import kidnox.eventbus.Subscribe;

public interface GenericSubscriber<T> {
    @Subscribe void obtain(T event);
}
