package kidnox.eventbus.test.exceptions;

import kidnox.eventbus.OnRegister;
import kidnox.eventbus.OnUnregister;
import kidnox.eventbus.Subscriber;

@Subscriber
public class ThrowingSubscriber2 {

    int onRegisterCount;
    int onUnregisterCount;

    @OnRegister public void onRegister() {
        onRegisterCount++;
        throw new RuntimeException();
    }

    @OnUnregister public void onUnregister() {
        onUnregisterCount++;
        throw new RuntimeException();
    }

    public int getOnRegisterCount() {
        return onRegisterCount;
    }

    public int getOnUnregisterCount() {
        return onUnregisterCount;
    }
}
