package kidnox.eventbus;


import kidnox.annotations.Nonnull;

public interface Dispatcher {

    void dispatchSubscribe(EventSubscriber subscriber, Object event);

    Dispatcher DEFAULT = new Dispatcher() {

        @Override
        public void dispatchSubscribe(EventSubscriber subscriber, Object event) {
            subscriber.invokeSubscribe(event);
        }
    };



    public interface Factory {

        public Dispatcher getDispatcher(@Nonnull String subscriberName);

        public static final Factory DEFAULT = new Factory() {

            @Override
            public Dispatcher getDispatcher(@Nonnull String subscriberName) {
                return Dispatcher.DEFAULT;
            }
        };

    }

}
