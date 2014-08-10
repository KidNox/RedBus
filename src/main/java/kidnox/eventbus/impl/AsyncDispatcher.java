package kidnox.eventbus.impl;

import kidnox.eventbus.Dispatcher;
import kidnox.eventbus.elements.EventSubscriber;

public abstract class AsyncDispatcher implements Dispatcher {

    protected abstract void dispatch(Runnable runnable);

    @Override public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
        dispatch(getRunnableSubscription(subscriber, event));
    }

    public boolean inCurrentThread(){
        return false;
    }

    static Runnable getRunnableSubscription(final EventSubscriber subscriber, final Object event) {
        return new Runnable() {
            @Override
            public void run() {
                subscriber.invoke(event);
            }
        };
    }

}
