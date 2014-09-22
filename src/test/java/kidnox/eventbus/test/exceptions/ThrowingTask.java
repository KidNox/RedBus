package kidnox.eventbus.test.exceptions;

import kidnox.eventbus.Execute;
import kidnox.eventbus.Task;

@Task
public class ThrowingTask {

    int executeCount;

    @Execute public void execute() throws TestException {
        executeCount++;
        throw new TestException();
    }

    public int getExecuteCount() {
        return executeCount;
    }
}
