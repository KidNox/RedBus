package kidnox.eventbus.test;

import kidnox.eventbus.ServiceClass;
import kidnox.eventbus.ServiceInstance;

@ServiceClass
public class EventService {

    @ServiceInstance public SimpleSubscriber createSimpleSubscriber() {
        return new SimpleSubscriber();
    }

}
