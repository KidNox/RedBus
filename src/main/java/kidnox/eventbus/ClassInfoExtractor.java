package kidnox.eventbus;

import kidnox.eventbus.elements.ClassProducers;
import kidnox.eventbus.elements.ClassSubscribers;
import kidnox.eventbus.elements.ClassType;

public interface ClassInfoExtractor {

    ClassType getTypeOf(Class clazz);

    ClassSubscribers getClassSubscribers(Class clazz);

    ClassProducers getClassProducers(Class clazz);

}
