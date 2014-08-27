package kidnox.eventbus.internal.extraction;

import kidnox.eventbus.internal.element.ElementInfo;

import java.lang.reflect.Method;
import java.util.Map;

public interface MethodExtractionStrategy {
    Map<Class, ElementInfo> extract(Method method, Class paramType, Class target, Map<Class, ElementInfo> elements);
}
