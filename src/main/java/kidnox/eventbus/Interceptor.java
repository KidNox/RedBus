package kidnox.eventbus;

public interface Interceptor {
    boolean intercept(Object event);
}
