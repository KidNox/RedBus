package kidnox.eventbus;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface AnnotationFinder {

    ClassInfo findClassInfo(Class clazz);


    static final class DefaultAnnotationFinder implements AnnotationFinder {

        static final Map<Class, ClassInfo> cache = new HashMap<Class, ClassInfo>();

        final ClassFilter classFilter;
        final Dispatcher.Factory dispatcherFactory;

        DefaultAnnotationFinder(ClassFilter filter, Dispatcher.Factory factory) {
            this.classFilter = filter == null ? ClassFilter.Filters.DEFAULT : filter;
            this.dispatcherFactory = factory == null ? Dispatcher.Factory.DEFAULT : factory;
        }

        @Override
        public ClassInfo findClassInfo(Class clazz) {
            ClassInfo classInfo = cache.get(clazz);
            if(classInfo != null) return classInfo;

            Map<Dispatcher, Map<Class, Method>> dispatchersToTypedMethodMap = null;

            for(Class mClass = clazz; !skipClass(mClass); mClass = mClass.getSuperclass()){

                Subscriber subscriberAnnotation = (Subscriber) clazz.getAnnotation(Subscriber.class);
                if(subscriberAnnotation != null){

                    final Map<Class, Method> subscribers = getSubscribedMethods(mClass);
                    if(subscribers.isEmpty())
                        continue;

                    Dispatcher dispatcher = dispatcherFactory.getDispatcher(subscriberAnnotation.value());
                    if(dispatcher == null)
                        dispatcher = Dispatcher.DEFAULT;

                    if(dispatchersToTypedMethodMap == null)
                        dispatchersToTypedMethodMap = new HashMap<Dispatcher, Map<Class, Method>>();

                    dispatchersToTypedMethodMap.put(dispatcher, subscribers);
                }
            }

            classInfo = dispatchersToTypedMethodMap == null
                    ? ClassInfo.EMPTY : new ClassInfo(dispatchersToTypedMethodMap);
            cache.put(clazz, classInfo);

            return classInfo;
        }

        protected boolean skipClass(Class clazz){
            return clazz == null || classFilter.skipClass(clazz) || clazz.isInterface();
        }

        private Map<Class, Method> getSubscribedMethods(Class targetClass){
            Map<Class, Method> classToMethodMap = null;

            for(Method method : targetClass.getDeclaredMethods()){

                if ((method.getModifiers() & Modifier.PUBLIC) == 0)
                    continue;

                if(method.getReturnType() != void.class)
                    continue;

                final Class[] params = method.getParameterTypes();

                if(params.length != 1)
                    continue;

                if(method.isAnnotationPresent(Subscribe.class)){
                    if(classToMethodMap == null)
                        classToMethodMap = new HashMap<Class, Method>();
                    classToMethodMap.put(params[0], method);
                }
            }
            return classToMethodMap == null ? Collections.<Class, Method>emptyMap() : classToMethodMap;
        }

    }

}
