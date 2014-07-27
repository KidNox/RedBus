package kidnox.eventbus;

public interface ClassInfoExtractor {

    ClassType getTypeOf(Class clazz);

    ClassSubscribers getClassSubscribers(Class clazz);

    ClassProducers getClassProducers(Class clazz);

}
