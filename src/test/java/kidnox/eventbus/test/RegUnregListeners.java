package kidnox.eventbus.test;

import kidnox.eventbus.Bus;

public class RegUnregListeners {

    volatile boolean onRegisterCall;
    volatile boolean onUnregisterCall;
    volatile Bus bus;

    public boolean isOnRegisterCall() {
        return onRegisterCall;
    }

    public void setOnRegisterCall(boolean onRegisterCall) {
        this.onRegisterCall = onRegisterCall;
    }

    public boolean isOnUnregisterCall() {
        return onUnregisterCall;
    }

    public void setOnUnregisterCall(boolean onUnregisterCall) {
        this.onUnregisterCall = onUnregisterCall;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }
}
