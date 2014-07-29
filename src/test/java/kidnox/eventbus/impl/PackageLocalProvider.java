package kidnox.eventbus.impl;

import kidnox.eventbus.*;

import java.util.List;
import java.util.Map;

public class PackageLocalProvider {

    public static List<EventSubscriber> getSubscribers(BusImpl bus, Object target) {
        return bus.instanceToSubscribersMap.get(target);
    }

    public static Map<Class, ClassType> getClassToTypeMap(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).classToTypeMap;
    }

    public static Map<Class, ClassSubscribers> getSubscibersCache(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).subscribersCache;
    }

    public static Map<Class, ClassProducers> getProducersCache(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).producersCache;
    }

    public static Map<String, Dispatcher> getDispatchersMap(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).dispatchersMap;
    }

    public static Dispatcher getDispatcher(String name, ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).getDispatcher(name);
    }

}
