package fix.java.util.concurrent.concurrent;

import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2015/3/12.
 */
public abstract class TakeSelf<T> implements Callable<TakeSelf<T>>, ITakeResult<T> {
    private T result;
    private Throwable ex;

    @Override
    public T getResult() throws Throwable {
        return result;
    }

    @Override
    public Throwable getThrowable() {
        return ex;
    }

    @Override
    public void setThrowable(Throwable ex) {
        this.ex = ex;
    }

    @Override
    public boolean isSuccess() {
        return (ex == null);
    }

    @Override
    public TakeSelf<T> call() throws Exception {
        result = take();
        return this;
    }
}
