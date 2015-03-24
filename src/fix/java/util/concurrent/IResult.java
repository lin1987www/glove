package fix.java.util.concurrent;

/**
 *  必須由最外面的 Runnable or Callable 來設定，這樣才能使得外部的 Timeout 跟 Retry Throwable 被抓到，有不會多重觸發的問題
 *
 * Created by Administrator on 2015/3/12.
 */
public interface IResult<T> {
    T getResult() throws Throwable;
    // 由 innerCallable 設定
    //void setResult(T result);

    Throwable getThrowable();
    // 由外部的Wrapper 進行設定
    void setThrowable(Throwable ex);

    boolean isSuccess();
}
