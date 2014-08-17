package kidnox.eventbus.test;

import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;

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

    Provider<ClassInfoExtractor> CLASS_INFO_EXTRACTOR_PROVIDER =
            new Provider<ClassInfoExtractor>() {
                @Override public ClassInfoExtractor get() {
                    return InternalFactory.createClassInfoExtractor(false);
                }
            };

    Provider<ClassInfoExtractor> CLASS_INFO_EXTRACTOR_VALIDATION_PROVIDER =
            new Provider<ClassInfoExtractor>() {
                @Override public ClassInfoExtractor get() {
                    return InternalFactory.createClassInfoExtractor(true);
                }
            };

}
