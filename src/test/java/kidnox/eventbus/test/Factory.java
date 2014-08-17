package kidnox.eventbus.test;

import kidnox.eventbus.EventDispatcher;
import kidnox.eventbus.internal.ClassInfoExtractor;
import kidnox.eventbus.internal.InternalFactory;

public interface Factory<Instance, Parameter> {
    Instance get(Parameter parameter);



}
