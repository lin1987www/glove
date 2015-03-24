package fix.java.util.concurrent;

/**
 * Created by Administrator on 2015/3/12.
 */
public interface ITake<T> {
    public abstract T take() throws Throwable;
}
