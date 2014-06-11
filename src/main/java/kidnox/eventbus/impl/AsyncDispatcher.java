package kidnox.eventbus.impl;

import kidnox.eventbus.Dispatcher;

public abstract class AsyncDispatcher implements Dispatcher {

    protected abstract void dispatch(Runnable runnable);

    @Override
    public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
        dispatch(getRunnableEventSubscriber(subscriber, event));
    }

    protected boolean isCurrentThread(){
        return false;
    }

    private static Runnable getRunnableEventSubscriber(final EventSubscriber subscriber, final Object event){
        return new Runnable() {
            @Override
            public void run() {
                subscriber.invoke(event);
            }
        };
    }

}
