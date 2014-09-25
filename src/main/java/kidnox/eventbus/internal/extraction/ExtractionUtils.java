package kidnox.eventbus.internal.extraction;

import kidnox.eventbus.*;
import kidnox.eventbus.internal.BusException;
import kidnox.eventbus.internal.ClassType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static kidnox.eventbus.internal.Utils.newHashMap;
import static kidnox.eventbus.internal.extraction.ClassInfoExtractorStrategy.*;
import static kidnox.eventbus.internal.extraction.ElementExtractionStrategy.*;

final class ExtractionUtils {

    static final Map<Class<? extends Annotation>, ElementExtractionStrategy> ELEMENT_STRATEGIES;
    static final Map<Class<? extends Annotation>, ClassInfoExtractorStrategy> CLASS_STRATEGIES;

    static {
        Map<Class<? extends Annotation>, ElementExtractionStrategy> mEES = newHashMap(8);
        mEES.put(OnRegister.class, REGISTER);
        mEES.put(OnUnregister.class, UNREGISTER);
        mEES.put(Subscribe.class, SUBSCRIBE);
        mEES.put(Produce.class, PRODUCE);
        mEES.put(Handle.class, HANDLE);
        mEES.put(Execute.class, EXECUTE);
        ELEMENT_STRATEGIES = Collections.unmodifiableMap(mEES);

        Map<Class<? extends Annotation>, ClassInfoExtractorStrategy> mCES = newHashMap(4);
        mCES.put(Subscriber.class, SUBSCRIBER);
        mCES.put(Producer.class, PRODUCER);
        mCES.put(Task.class, TASK);
        CLASS_STRATEGIES = Collections.unmodifiableMap(mCES);
    }

    static Map<Class<? extends Annotation>, ElementExtractionStrategy> getElementStrategiesFor
            (Class<? extends Annotation>... args) {
        Map<Class<? extends Annotation>, ElementExtractionStrategy> map = newHashMap(args.length);
        for(Class<? extends Annotation> key : args) {
            map.put(key, ELEMENT_STRATEGIES.get(key));
        }
        return Collections.unmodifiableMap(map);
    }

    static void checkAnnotationValue(Class clazz, String annotationValue, String baseValue, Class child) {
        if (!baseValue.equals(annotationValue)){
            throw new BusException("dispatchers for child and parent classes does not match: child class = %s, " +
                    "dispatcher = %s, parent class = %s, dispatcher = %s.",
                    child.getName(), baseValue, clazz.getName(), annotationValue);
        }
    }

    static void throwBadMethodException(Method method, String description) {
        throw new BusException("Method %s %s", method, description);
    }

}
