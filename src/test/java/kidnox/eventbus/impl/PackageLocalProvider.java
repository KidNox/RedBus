package kidnox.eventbus.impl;


import kidnox.eventbus.ClassInfoExtractor;
import kidnox.eventbus.ClassInfo;

import java.util.List;

public class PackageLocalProvider {

    public static List<EventSubscriber> getSubscribers(Object target, ClassInfo classInfo) {
        return BusImpl.getSubscribers(target, classInfo);
    }

    public static List<EventSubscriber> getSubscribers(Object target, ClassInfoExtractor classInfoExtractor) {
        ClassInfo classInfo = classInfoExtractor.findClassInfo(target.getClass());
        return getSubscribers(target, classInfo);
    }

}
