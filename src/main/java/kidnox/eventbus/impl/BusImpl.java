package kidnox.eventbus.impl;

import kidnox.annotations.NotNull;
import kidnox.common.Pair;
import kidnox.eventbus.*;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Map.Entry;

public class BusImpl implements Bus {

    protected final Map<Object, List<EventSubscriber>> instanceToSubscribersMap = new HashMap<Object, List<EventSubscriber>>();
    protected final Map<Class, Set<EventSubscriber>> eventTypeToSubscribersMap = new HashMap<Class, Set<EventSubscriber>>();

    protected final String name;
    protected final DeadEventHandler deadEventHandler;

    protected final ClassInfoExtractor classInfoExtractor;

    public BusImpl(String name, ClassInfoExtractor classInfoExtractor, DeadEventHandler deadEventHandler) {
        this.name = name;
        this.deadEventHandler = deadEventHandler;
        this.classInfoExtractor = classInfoExtractor;
    }

    @Override
    public void register(Object target) {
        final ClassInfo classInfo = getClassInfo(target.getClass());
        if(ClassInfo.isNullOrEmpty(classInfo)){
            instanceToSubscribersMap.put(target, Collections.<EventSubscriber>emptyList());
            return;
        }
        final List<EventSubscriber> subscribers = getSubscribers(target, classInfo);

        if (instanceToSubscribersMap.put(target, subscribers) != null)
            throwRuntimeException("register", target, " already registered");

        for (EventSubscriber subscriber : subscribers) {
            Set<EventSubscriber> set = eventTypeToSubscribersMap.get(subscriber.eventClass);
            if (set == null) {
                set = new HashSet<EventSubscriber>();
                eventTypeToSubscribersMap.put(subscriber.eventClass, set);
            }
            if (!set.add(subscriber))
                throwRuntimeException("register", subscriber, " already registered");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void unregister(Object target) {
        final List<EventSubscriber> subscribers = instanceToSubscribersMap.remove(target);
        if (subscribers == null)
            throwRuntimeException("unregister", target, " not registered");

        if(subscribers.isEmpty())
            return;

        for (EventSubscriber subscriber : subscribers) {
            eventTypeToSubscribersMap.get(subscriber.eventClass).remove(subscriber);
        }
    }

    @Override
    public void post(Object event) {
        Set<EventSubscriber> set = eventTypeToSubscribersMap.get(event.getClass());
        if (kidnox.utils.Collections.notEmpty(set)) {
            for (EventSubscriber subscriber : set) {
                subscriber.dispatch(event);
            }
        } else {
            onDeadEvent(event);
        }
    }

    protected ClassInfo getClassInfo(Class clazz){
        return classInfoExtractor.findClassInfo(clazz);
    }

    protected void onDeadEvent(Object event){
        if(deadEventHandler != null)
            deadEventHandler.onDeadEvent(event);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' + name + '}';
    }

    static @NotNull List<EventSubscriber> getSubscribers(@NotNull Object target, @NotNull ClassInfo classInfo){
        final LinkedList<EventSubscriber> subscribers = new LinkedList<EventSubscriber>();
        for(Pair<Dispatcher, Map<Class, Method>> dispatcherEntry : classInfo.dispatchersToTypedMethodList){
            final Dispatcher dispatcher = dispatcherEntry.left;
            for(Entry<Class, Method> methodEntry : dispatcherEntry.right.entrySet()){
                subscribers.add(new EventSubscriber(methodEntry.getKey(), target, methodEntry.getValue(), dispatcher));
            }
        }
        return subscribers;
    }

    protected void throwRuntimeException(String action, Object cause, String message) {
        throw new IllegalStateException(action + " was failed in " + toString() + ", " + cause + message);
    }
}