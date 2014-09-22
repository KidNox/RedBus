package kidnox.eventbus.test.exceptions;

import kidnox.eventbus.Execute;
import kidnox.eventbus.OnRegister;
import kidnox.eventbus.OnUnregister;
import kidnox.eventbus.Task;

@Task
public class ThrowingTask2 {

    int onRegisterCount;
    int executeCount;
    int onUnregisterCount;

    @OnRegister public void onRegister() throws TestException {
        onRegisterCount++;
        throw new TestException();
    }

    @Execute public void execute() throws TestException {
        executeCount++;
        throw new TestException();
    }

    @OnUnregister public void onUnregister() throws TestException {
        onUnregisterCount++;
        throw new TestException();
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
