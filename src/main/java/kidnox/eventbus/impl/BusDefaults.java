package kidnox.eventbus.impl;

import kidnox.common.Factory;
import kidnox.eventbus.ClassInfoExtractor;
import kidnox.eventbus.ClassFilter;
import kidnox.eventbus.Dispatcher;

/**internal*/
public final class BusDefaults {

    public static final Dispatcher DISPATCHER = new Dispatcher() {
        @Override public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
            subscriber.invoke(event);
        }
    };

    public static ClassInfoExtractor createDefaultExtractor(ClassFilter filter, Factory<Dispatcher, String> factory) {
        return new ClassInfoExtractorImpl(filter, factory);
    }

    public static Factory<Dispatcher, String> createDefaultDispatcherFactory() {
        return new Factory<Dispatcher, String>() {
            @Override public Dispatcher get(String s) {
                return BusDefaults.DISPATCHER;
            }
        };
    }

}
