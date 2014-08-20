package kidnox.eventbus.async;

import java.util.concurrent.Executor;
//TODO replace with dispatcher, move all to utils
public interface Worker extends Executor {

    String getName();

    void dismiss(boolean mustFinishTasks);

    boolean inWorkerThread();

    @SuppressWarnings("NullableProblems")
    @Override void execute(Runnable command);
}
