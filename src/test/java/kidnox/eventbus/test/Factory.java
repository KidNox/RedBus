package kidnox.eventbus.test;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;

public interface Factory<Instance, Parameter> {
    Instance get(Parameter parameter);

//    Factory<ClassInfoExtractor, EventDispatcher.Factory> CLASS_INFO_EXTRACTOR_FACTORY =
//            new Factory<ClassInfoExtractor, EventDispatcher.Factory>() {
//                @Override public ClassInfoExtractor get(EventDispatcher.Factory factory) {
//                    return InternalFactory.createClassInfoExtractor(factory, false);
//                }
//            };
//
//    Factory<ClassInfoExtractor, EventDispatcher.Factory> CLASS_INFO_EXTRACTOR_VALIDATION_FACTORY =
//            new Factory<ClassInfoExtractor, EventDispatcher.Factory>() {
//                @Override public ClassInfoExtractor get(EventDispatcher.Factory factory) {
//                    return InternalFactory.createClassInfoExtractor(factory, true);
//                }
//            };



}
