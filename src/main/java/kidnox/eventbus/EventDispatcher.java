package kidnox.eventbus;

public interface EventDispatcher {

    String MAIN     = "main";
    String WORKER   = "worker";

    boolean isDispatcherThread();

    void dispatch(Runnable event);

    //TODO figure out about lazy dispatchers
    interface Factory {
        EventDispatcher getDispatcher(String name);
    }

}
