package kidnox.eventbus.test;

import kidnox.eventbus.util.SingleThreadEventDispatcher;

public class NamedAsyncEventDispatcher extends SingleThreadEventDispatcher {

    final String name;

    public NamedAsyncEventDispatcher(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}