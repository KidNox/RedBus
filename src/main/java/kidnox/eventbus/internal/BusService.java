package kidnox.eventbus.internal;

import java.util.List;

public interface BusService {

    List<AsyncElement> registerSubscribers(Object target, ClassInfo classInfo);

    List<AsyncElement> registerProducers(Object target, ClassInfo classInfo);

    void unregisterSubscribers(List<AsyncElement> subscribers);

    void unregisterProducers(List<AsyncElement> producers);

    void post(Object event);

    void dispatch(AsyncElement subscriber, Object event);

    void dispatch(AsyncElement producer, AsyncElement subscriber);

    void dispatch(AsyncElement producer);

}
