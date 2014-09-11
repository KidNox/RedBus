package kidnox.eventbus;

public interface Dispatcher {

    boolean isDispatcherThread();

    void dispatch(Runnable event);

    //TODO figure out about lazy dispatchers
    interface Factory {
        Dispatcher getDispatcher(String name);
    }

}
