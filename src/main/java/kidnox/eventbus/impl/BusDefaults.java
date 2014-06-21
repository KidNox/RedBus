package kidnox.eventbus.impl;

import kidnox.annotations.Internal;
import kidnox.common.Factory;
import kidnox.eventbus.ClassInfoExtractor;
import kidnox.eventbus.ClassFilter;
import kidnox.eventbus.Dispatcher;

@Internal
public class BusDefaults {

    public static final Dispatcher DISPATCHER = new Dispatcher() {
        @Override
        public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
            subscriber.invoke(event);
        }
    };

    public static ClassInfoExtractor createDefaultAnnotationFinder(ClassFilter filter, Factory<String, Dispatcher> factory) {
        return new ClassInfoExtractorImpl(filter, factory);
    }

    public static Factory<String, Dispatcher> createDefaultDispatcherFactory() {
        return new Factory<String, Dispatcher>() {
            @Override public Dispatcher get(String s) {
                return BusDefaults.DISPATCHER;
            }
        };
    }

}
