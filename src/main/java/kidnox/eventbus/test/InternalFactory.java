package kidnox.eventbus.test;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.EventLogger;
import kidnox.eventbus.elements.EventProducer;
import kidnox.eventbus.elements.EventSubscriber;
import kidnox.eventbus.impl.ClassInfoExtractorImpl;
import kidnox.eventbus.impl.ClassInfoExtractorValidation;

public final class InternalFactory {

    public static final EventDispatcher CURRENT_THREAD_DISPATCHER = new EventDispatcher() {
        @Override public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
            subscriber.invoke(event);
        }

        @Override public void dispatchProduce(EventProducer eventProducer, EventSubscriber eventSubscriber) {
            eventSubscriber.invoke(eventProducer.invoke(null));
        }
    };

    public static ClassInfoExtractor createClassInfoExtractor(EventDispatcher.Factory factory, boolean validate) {
        if(validate) return new ClassInfoExtractorValidation(factory);
        else return new ClassInfoExtractorImpl(factory);
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

    public static EventLogger getStubLogger() {
        return new EventLogger() {
            @Override public void logEvent(Object event, Object target, String what) { }
        };
    }

}
