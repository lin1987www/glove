# glove

Wrapping **Runnable** and **Callable**, Let them would be more controllable!

## Timeout

	long sleepMs = 2000;
	long timeoutMs = 3000;
	// Thread.Sleep(2000)
	TestTimeoutRunnable task = new TestTimeoutRunnable("God", sleepMs);
	// TimeoutRunnable Wrap ! 
	TimeoutRunnable timeoutTask = new TimeoutRunnable(task, timeoutMs, TimeUnit.MILLISECONDS);
	Future<?> future = oneThreadExecutor.submit(timeoutTask);
	try {
		future.get();
	} catch (Throwable ex) {
		fail(ex.toString());
	}

## Retry

	int throwTimes = 2;
	// TestRetryRunnable will throw 2 times exception
	TestRetryRunnable task = new TestRetryRunnable(throwTimes);
	// RetryRunnable be set how times exception would allow
	RetryRunnable retryTask = new RetryRunnable(task, throwTimes);
	Future<?> future = oneThreadExecutor.submit(retryTask);
	future.get();

## ReRun by Handle Exception

	// If handle exception success, return true.
	public interface IHandleException {
		boolean handleException(Throwable ex);
	}

	// Implement this interface would make rerun as possible
	public interface IReRunnable extends Runnable, IHandleException {
	}
	
	// Example:
	// Throw NullPointerException set y not null.
	// Throw ArithmeticException set y not zero.
	public class TestReCallable implements IReCallable<Integer> {
		private Integer x = 1;
		private Integer y = null;
	
		@Override
		public boolean handleException(Throwable ex) {
			if(ex instanceof NullPointerException){
				y = 0;
				return true;
			}if(ex instanceof  java.lang.ArithmeticException){
				y = 1;
				return true;
			}
			return false;
		}
	
		@Override
		public Integer call() throws Exception {
			int result = x/y;
			return result;
		}
	}