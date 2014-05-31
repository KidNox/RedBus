package kidnox.eventbus;

public interface Bus {

    void register(Object target);
    void unregister(Object target);
    void post(Object event);

}
