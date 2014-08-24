package kidnox.eventbus;

public interface EventInterceptor {
    boolean intercept(Object event);
}
