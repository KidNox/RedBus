package kidnox.eventbus.test;

import kidnox.eventbus.Execute;
import kidnox.eventbus.OnRegister;
import kidnox.eventbus.OnUnregister;
import kidnox.eventbus.Task;

@Task
public class SimpleTask2 {

    int onRegisterCount;
    int executeCount;
    int onUnregisterCount;

    @OnRegister public void onRegister() {
        onRegisterCount++;
    }

    @Execute public void execute() {
        executeCount++;
    }

    @OnUnregister public void onUnregister() {
        onUnregisterCount++;
    }

    public int getOnRegisterCount() {
        return onRegisterCount;
    }

    public int getExecuteCount() {
        return executeCount;
    }

    public int getOnUnregisterCount() {
        return onUnregisterCount;
    }

}
