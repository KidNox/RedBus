package kidnox.eventbus.test;

import kidnox.eventbus.test.simple.SimpleNone;
import kidnox.eventbus.test.simple.SimpleProducer;
import kidnox.eventbus.test.simple.SimpleSubscriber;

public interface Provider<T> {

    T get();

    Provider<SimpleSubscriber> SIMPLE_SUBSCRIBER_PROVIDER = new Provider<SimpleSubscriber>() {
        @Override public SimpleSubscriber get() {
            return new SimpleSubscriber();
        }
    };

    Provider<SimpleProducer> SIMPLE_PRODUCER_PROVIDER = new Provider<SimpleProducer>() {
        @Override public SimpleProducer get() {
            return new SimpleProducer();
        }
    };



    Provider<SimpleNone> SIMPLE_NONE_PROVIDER = new Provider<SimpleNone>() {
        @Override public SimpleNone get() {
            return new SimpleNone();
        }
    };

}
