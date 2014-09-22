package kidnox.eventbus.test;

import kidnox.eventbus.Bus;

public class RegUnregListeners {

    volatile int onRegisterCallCount;
    volatile int onUnregisterCallCount;
    volatile Bus bus;

    public int getOnRegisterCallCount() {
        return onRegisterCallCount;
    }

    public int getOnUnregisterCallCount() {
        return onUnregisterCallCount;
    }

    public void onRegister() {
        onRegisterCallCount++;
    }

    public void onUnregister() {
        onUnregisterCallCount++;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }
}
