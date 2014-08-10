package kidnox.eventbus.internal;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.elements.EventProducer;
import kidnox.eventbus.elements.EventSubscriber;
import kidnox.eventbus.impl.ClassInfoExtractorImpl;
import kidnox.eventbus.impl.ClassInfoExtractorValidation;

/**internal*/
public final class InternalFactory {

    public static final EventDispatcher CURRENT_THREAD_DISPATCHER = new EventDispatcher() {
        @Override public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
            subscriber.invoke(event);
        }

        @Override public void dispatchProduce(EventProducer eventProducer, EventSubscriber eventSubscriber) {
            eventSubscriber.invoke(eventProducer.invoke(null));
        }
    };

    public static ClassInfoExtractor createDefaultExtractor(EventDispatcher.Factory factory) {
        return new ClassInfoExtractorImpl(factory);
    }

    public static ClassInfoExtractor createValidationExtractor(EventDispatcher.Factory factory) {
        return new ClassInfoExtractorValidation(factory);
    }

    public static EventDispatcher.Factory createDefaultEventDispatcherFactory() {
        return new EventDispatcher.Factory() {
            @Override public EventDispatcher getDispatcher(String name) {
                if(name.isEmpty()) {
                    return InternalFactory.CURRENT_THREAD_DISPATCHER;
                } else {
                    throw new IllegalArgumentException("Dispatcher ["+name+"] not found");
                }
            }
        };
    }

}
