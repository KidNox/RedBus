package kidnox.eventbus.internal;

import kidnox.eventbus.elements.ClassProducers;
import kidnox.eventbus.elements.ClassSubscribers;

public interface ClassInfoExtractor {

    ClassType getTypeOf(Class clazz);

    ClassSubscribers getClassSubscribers(Class clazz);

    ClassProducers getClassProducers(Class clazz);

}
