package fix.java.util.concurrent.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/3/12.
 */
public class TakeSelfBuilder<T extends TakeSelf<T>> implements Callable<T> {
    private T task;
    private Callable<T> wrapper;

    public TakeSelfBuilder(T task) {
        if (task == null) {
            throw new NullPointerException();
        }
        this.task = task;
        this.wrapper = (Callable<T>) task;
    }

    public TakeSelfBuilder<T> retry(int maxTimes) {
        this.wrapper = new RetryCallable<T>(this.wrapper, maxTimes);
        return this;
    }

    public TakeSelfBuilder<T> timeout(long timeout, TimeUnit unit) {
        this.wrapper = new TimeoutCallable<T>(this.wrapper, timeout, unit);
        return this;
    }

    @Override
    public T call() throws Exception {
        try {
            wrapper.call();
        } catch (Throwable ex) {
            task.setThrowable(ex);
        }
        // 他只會將他自己丟出來而已  Callable<T>
        return (T) task;
    }
}
