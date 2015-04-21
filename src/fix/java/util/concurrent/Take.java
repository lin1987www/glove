package fix.java.util.concurrent;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2015/3/12.
 */
public abstract class Take<T> implements Callable<Take<T>>, IHandleException {
    // TakeSelfBuilder will set startTimeMillis and stopTimeMillis.
    protected long startTimeMillis = 0;
    protected long stopTimeMillis = 0;
    protected AtomicBoolean mCancelled = new AtomicBoolean(false);
    protected AtomicBoolean mCompleted = new AtomicBoolean(false);
    protected Throwable ex;
    protected String tag;

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getStopTimeMillis() {
        return stopTimeMillis;
    }

    public long getElapsedTimeMillis() {
        return stopTimeMillis - startTimeMillis;
    }

    public final boolean isCompleted() {
        return mCompleted.get();
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public void cancel() {
        mCancelled.set(true);
    }

    public Throwable getThrowable() {
        return ex;
    }

    public void setThrowable(Throwable ex) {
        this.ex = ex;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag.toString();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean equalsTag(String tag) {
        if (this.tag == null) {
            return false;
        }
        return this.tag.equals(tag);
    }

    public boolean isSuccess() {
        return (ex == null) && isCompleted() && !isCancelled();
    }

    public abstract T take() throws Throwable;

    public Take<T> call() throws Exception {
        while (true) {
            try {
                if (!isCancelled()) {
                    take();
                }
                mCompleted.set(true);
                break;
            } catch (Throwable ex) {
                if (handleException(ex)) {
                    continue;
                } else {
                    ExceptionHelper.throwException(this.toString(), ex);
                }
            }
        }
        return this;
    }
}
