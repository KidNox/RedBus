package kidnox.eventbus.internal;

public interface ElementsGroup {

    void registerGroup(Object target);

    void unregisterGroup();


    ElementsGroup EMPTY = new ElementsGroup() {
        @Override public void registerGroup(Object target) { }

        @Override public void unregisterGroup() { }
    };
}
