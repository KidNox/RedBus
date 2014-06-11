package kidnox.eventbus.impl;

import kidnox.annotations.Internal;
import kidnox.annotations.NotNull;
import kidnox.eventbus.AnnotationFinder;
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

    public static AnnotationFinder createDefaultAnnotationFinder(ClassFilter filter, Dispatcher.Factory factory) {
        return new AnnotationFinderImpl(filter, factory);
    }


    public static Dispatcher.Factory createDefaultDispatcherFactory() {
        return new Dispatcher.Factory() {
            @Override
            public Dispatcher getDispatcher(@NotNull String subscriberName) {
                return BusDefaults.DISPATCHER;
            }
        };
    }

}
