package kidnox.eventbus.internal;

public final class BusException extends RuntimeException {

    private static final long serialVersionUID = -2544973023435911787L;

    public BusException() {}

    public BusException(String message) {
        super(message);
    }

    public BusException(String message, Throwable cause) {
        super(message, cause);
    }
}
