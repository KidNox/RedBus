package kidnox.eventbus.internal;

public final class BusException extends RuntimeException {

    private static final long serialVersionUID = 4258951759008513238L;

    public BusException(String template, Object... args) {
        this(String.format(template, args));
    }

    public BusException(String message) {
        super(message);
    }

}
