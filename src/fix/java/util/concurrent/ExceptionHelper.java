package fix.java.util.concurrent;

/**
 * Created by Administrator on 2015/3/6.
 */
public class ExceptionHelper {
    public static void throwException(String taskName, Throwable ex) throws Exception {
    	printException(taskName,ex);
        throw new Exception(ex);
    }

    public static void throwRuntimeException(String taskName, Throwable ex) throws RuntimeException {
    	printException(taskName,ex);
        throw new RuntimeException(ex);
    }

    public static void printException(String taskName, Throwable ex)  {
    	Thread thread = Thread.currentThread();
        System.err.println(String.format("Exception in thread \"%s\" %s %s", thread.toString(),ex.toString(),taskName));
        ex.printStackTrace();
    }
}
