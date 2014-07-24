package kidnox.eventbus.async;

import java.util.concurrent.Executor;

public interface Worker extends Executor {

    String getName();

    void dismiss(boolean mustFinishTasks);

    boolean inWorkerThread();

    @SuppressWarnings("NullableProblems")
    @Override void execute(Runnable command);
}
