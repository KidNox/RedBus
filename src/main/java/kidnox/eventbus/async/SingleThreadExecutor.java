package kidnox.eventbus.async;

import java.util.concurrent.*;

public class SingleThreadExecutor extends ThreadPoolExecutor {

    public SingleThreadExecutor() {
        this(new LinkedBlockingQueue<Runnable>(), new SingleThreadFactory());
    }

    public SingleThreadExecutor(BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(1, 1, 0, TimeUnit.NANOSECONDS, workQueue, threadFactory);
    }


}
