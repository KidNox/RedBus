package kidnox.eventbus.internal;

import kidnox.eventbus.ClassInfoExtractor;
import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.impl.BusDefaults;

public interface Factory<Instance, Parameter> {
    Instance get(Parameter parameter);

    Factory<ClassInfoExtractor, Dispatcher.Factory> CLASS_INFO_EXTRACTOR_FACTORY =
            new Factory<ClassInfoExtractor, Dispatcher.Factory>() {
                @Override public ClassInfoExtractor get(Dispatcher.Factory factory) {
                    return BusDefaults.createDefaultExtractor(factory);
                }
            };

    Factory<ClassInfoExtractor, Dispatcher.Factory> CLASS_INFO_EXTRACTOR_VALIDATION_FACTORY =
            new Factory<ClassInfoExtractor, Dispatcher.Factory>() {
                @Override public ClassInfoExtractor get(Dispatcher.Factory factory) {
                    return BusDefaults.createValidationExtractor(factory);
                }
            };



}
