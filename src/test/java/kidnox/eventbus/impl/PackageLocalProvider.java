package kidnox.eventbus.impl;

import kidnox.eventbus.elements.EventSubscriber;
import kidnox.eventbus.internal.ClassInfo;
import kidnox.eventbus.internal.ClassInfoExtractor;

import java.util.List;
import java.util.Map;

public class PackageLocalProvider {

    public static List<EventSubscriber> getSubscribers(AsyncBus bus, Object target) {
        return bus.instanceToSubscribersMap.get(target);
    }

    public static Map<Class, ClassInfo> getClassToInfoMap(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).classToInfoMap;
    }
/*
    public static Map<Class, ClassSubscribers> getSubscibersCache(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).subscribersCache;
    }

    public static Map<Class, ClassProducers> getProducersCache(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).producersCache;
    }

    public static Map<String, EventDispatcher> getDispatchersMap(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).dispatchersMap;
    }

    public static EventDispatcher getDispatcher(String name, ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractorImpl)classInfoExtractor).getDispatcher(name);
    }*/

}
