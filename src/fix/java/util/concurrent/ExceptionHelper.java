package fix.java.util.concurrent;

/**
 * Created by Administrator on 2015/3/6.
 */
public class ExceptionHelper {
    public static void throwRuntimeException(String taskName, Throwable ex) throws RuntimeException {
        System.err.println("Exception in executing: " + taskName + ". It will no longer be run!");
        ex.printStackTrace();
        Thread thread = Thread.currentThread();
        if (thread.getUncaughtExceptionHandler() != null) {
            thread.getUncaughtExceptionHandler().uncaughtException(thread, ex);
        } else if(thread.getThreadGroup() != null){
            thread.getThreadGroup().uncaughtException(thread,ex);
        }
        throw new RuntimeException(ex);
    }
}
