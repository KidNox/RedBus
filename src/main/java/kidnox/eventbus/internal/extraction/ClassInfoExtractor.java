package kidnox.eventbus.internal.extraction;

import kidnox.eventbus.internal.ClassInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

import static kidnox.eventbus.internal.Utils.isNullOrEmpty;
import static kidnox.eventbus.internal.Utils.newHashMap;
import static kidnox.eventbus.internal.extraction.ExtractionUtils.CLASS_STRATEGIES;

public interface ClassInfoExtractor {

    ClassInfo getClassInfo(Class clazz);


    class ClassInfoExtractorImpl implements ClassInfoExtractor {
        final Map<Class, ClassInfo> classInfoCache = newHashMap();

        @SuppressWarnings("unchecked")
        @Override public ClassInfo getClassInfo(Class clazz) {
            ClassInfo info = classInfoCache.get(clazz);
            if(info != null) return info;

            final Annotation[] annotations = clazz.getAnnotations();
            if(!isNullOrEmpty(annotations)) {
                for (Annotation annotation : annotations) {
                    ClassInfoExtractorStrategy extractionStrategy = CLASS_STRATEGIES.get(annotation.annotationType());
                    if(extractionStrategy != null) {
                        info = extractionStrategy.extract(clazz, annotation);
                        break;
                    }
                }
            }
            if(info == null) info = new ClassInfo(clazz);//info for none type
            classInfoCache.put(clazz, info);
            return info;
        }
    }

}
