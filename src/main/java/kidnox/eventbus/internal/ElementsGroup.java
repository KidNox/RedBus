package kidnox.eventbus.internal;

public interface ElementsGroup {

    void unregisterGroup();


    ElementsGroup EMPTY = new ElementsGroup() {
        @Override public void unregisterGroup() { }
    };
}
