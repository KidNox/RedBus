package kidnox.eventbus.test.bad;

public class BadClass {

    public final String cause;

    public BadClass(String cause) {
        this.cause = cause;
    }

    @Override public String toString() {
        return cause;
    }
}
