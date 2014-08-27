package kidnox.eventbus.internal.extraction;

import kidnox.eventbus.internal.ClassInfo;

public interface ClassExtractionStrategy<T> {
    ClassInfo extract(Class clazz, T annotation);
}
