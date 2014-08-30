package kidnox.eventbus.internal;
//TODO replace with abstract class with AsyncBus reference?
public interface ElementsGroup {

    void registerGroup(Object target);

    void unregisterGroup();


    ElementsGroup EMPTY = new ElementsGroup() {
        @Override public void registerGroup(Object target) { }

        @Override public void unregisterGroup() { }
    };
}
