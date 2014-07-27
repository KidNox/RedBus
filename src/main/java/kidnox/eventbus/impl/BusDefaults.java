package kidnox.eventbus.impl;

import kidnox.eventbus.ClassInfoExtractor;
import kidnox.eventbus.Dispatcher;

/**internal*/
public final class BusDefaults {

    public static final Dispatcher DISPATCHER = new Dispatcher() {
        @Override public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
            subscriber.invoke(event);
        }
    };

    public static ClassInfoExtractor createDefaultExtractor(Dispatcher.Factory factory) {
        return new ClassInfoExtractorImpl(factory);
    }

    public static ClassInfoExtractor createValidationExtractor(Dispatcher.Factory factory) {
        return new ClassInfoExtractorValidation(factory);
    }

    public static Dispatcher.Factory createDefaultDispatcherFactory() {
        return new Dispatcher.Factory() {
            @Override public Dispatcher getDispatcher(String name) {
                return BusDefaults.DISPATCHER;
            }
        };
    }

}
