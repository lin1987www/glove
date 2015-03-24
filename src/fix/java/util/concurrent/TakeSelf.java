package fix.java.util.concurrent;

import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2015/3/12.
 */
public abstract class TakeSelf<T> implements Callable<TakeSelf<T>>, ITakeResult<T> {
    private T result;
    private Throwable ex;
    // TakeSelfBuilder will set startTimeMillis and stopTimeMillis.
	long startTimeMillis = 0;
	long stopTimeMillis = 0;
    
	public long getStartTimeMillis(){
		return startTimeMillis;
	}
	
	public long getStopTimeMillis(){
		return stopTimeMillis;
	}
	
	public long getElapsedTimeMillis(){
		return stopTimeMillis - startTimeMillis;
	}
	
    public T getResult(T defaultValue) {
    	if(isSuccess()){
    		return result;
    	}
        return defaultValue;
    }
	
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
        while (true) {
            try {
            	result = take();
                break;
            } catch (Throwable ex) {
                if (handleException(ex)) {
                    continue;
                }else {
                    ExceptionHelper.throwException(this.toString(), ex);
                }
            }
        }
        return this;
    }
}
