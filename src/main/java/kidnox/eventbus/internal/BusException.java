package kidnox.eventbus.internal;

public final class BusException extends RuntimeException {
    public BusException() {
    }

    public BusException(String message) {
        super(message);
    }

    public BusException(String message, Throwable cause) {
        super(message, cause);
    }
}
