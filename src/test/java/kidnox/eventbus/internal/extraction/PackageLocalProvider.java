package kidnox.eventbus.internal.extraction;

import kidnox.eventbus.internal.ClassInfo;

import java.util.Map;

public class PackageLocalProvider {

    public static Map<Class, ClassInfo> getClassToInfoMap(ClassInfoExtractor classInfoExtractor) {
        return ((ClassInfoExtractor.ClassInfoExtractorImpl)classInfoExtractor).classInfoCache;
    }

}
