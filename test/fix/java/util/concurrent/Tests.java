package fix.java.util.concurrent;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Tests {

	ScheduledExecutorFix oneThreadExecutor;
	ScheduledExecutorFix twoThreadExecutor;
	ThreadPoolExecutor in2secExecutor;

	@Before
	public void setUp() throws Exception {
		oneThreadExecutor = new ScheduledExecutorFix(1);
		twoThreadExecutor = new ScheduledExecutorFix(2);
		in2secExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 2L,
				TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	@After
	public void tearDown() throws Exception {
		oneThreadExecutor.shutdown();
		twoThreadExecutor.shutdown();
		in2secExecutor.shutdown();
	}

	@Test
	public void testRetryRunnable() throws Throwable {
		int throwTimes = 2;
		TestRetryRunnable task = new TestRetryRunnable(throwTimes);
		RetryRunnable retryTask = new RetryRunnable(task, throwTimes);
		Future<?> future = oneThreadExecutor.submit(retryTask);
		future.get();
		Assert.assertEquals(throwTimes, task.throwTimes);
	}

	@Test
	public void testTimeoutRunnable() {
		// 時間內 執行完畢
		long sleepMs = 2000;
		long timeoutMs = 3000;
		TestTimeoutRunnable task = new TestTimeoutRunnable("God", sleepMs);
		TimeoutRunnable timeoutTask = new TimeoutRunnable(task, timeoutMs,
				TimeUnit.MILLISECONDS);
		Future<?> future = oneThreadExecutor.submit(timeoutTask);
		try {
			future.get();
		} catch (Throwable ex) {
			fail(ex.toString());
		}
	}

	@Test
	public void testTimeoutRunnable2() {
		long sleepMs = 2000;
		long timeoutMs = 3000;
		TestTimeoutRunnable task = new TestTimeoutRunnable("Tom", sleepMs);
		TestTimeoutRunnable task2 = new TestTimeoutRunnable("John", sleepMs);
		TimeoutRunnable timeoutTask = new TimeoutRunnable(task, timeoutMs,
				TimeUnit.MILLISECONDS);
		TimeoutRunnable timeoutTask2 = new TimeoutRunnable(task2, timeoutMs,
				TimeUnit.MILLISECONDS);
		Future<?> future = oneThreadExecutor.submit(timeoutTask);
		Future<?> future2 = oneThreadExecutor.submit(timeoutTask2);
		try {
			future.get();
			future2.get();
		} catch (Throwable ex) {
			fail(ex.toString());
		}
	}

	@Test
	public void testTimeoutRunnable3() {
		// Parallel execute, because twoThreadExecutor has two thread
		long sleepMs = 2000;
		long timeoutMs = 3000;
		TestTimeoutRunnable task = new TestTimeoutRunnable("Tom-Parallel",
				sleepMs);
		TestTimeoutRunnable task2 = new TestTimeoutRunnable("John-Parallel",
				sleepMs);
		TimeoutRunnable timeoutTask = new TimeoutRunnable(task, timeoutMs,
				TimeUnit.MILLISECONDS);
		TimeoutRunnable timeoutTask2 = new TimeoutRunnable(task2, timeoutMs,
				TimeUnit.MILLISECONDS);
		Future<?> future = twoThreadExecutor.submit(timeoutTask);
		Future<?> future2 = twoThreadExecutor.submit(timeoutTask2);
		try {
			future.get();
			future2.get();
		} catch (Throwable ex) {
			fail(ex.toString());
		}
	}

	@Test
	public void testReRunnable() {
		TestReRunnable task = new TestReRunnable();
		HandleReRunnable handleTask = new HandleReRunnable(task);
		Future<?> future = oneThreadExecutor.submit(handleTask);
		try {
			future.get();
		} catch (Throwable ex) {
			fail(ex.toString());
		}
	}

	@Test
	public void testReCallable() {
		TestReCallable task = new TestReCallable();
		HandleReCallable<Integer> handleTask = new HandleReCallable<Integer>(
				task);
		Future<Integer> future = oneThreadExecutor.submit(handleTask);
		try {
			Integer result = future.get();
			Assert.assertTrue(result.equals(1));
		} catch (Throwable ex) {
			fail(ex.toString());
		}
	}

	@Test
	public void testRetryTimeoutRunnable() {
		// 每執行一次 就會快1秒，必須在3秒妹執行完
		TestRetryTimeoutRunnable task = new TestRetryTimeoutRunnable("Faster",
				5);
		TimeoutRunnable timeoutTask = new TimeoutRunnable(task, 3,
				TimeUnit.SECONDS);
		RetryRunnable retryTimeoutTask = new RetryRunnable(timeoutTask, 4);
		Future<?> future = oneThreadExecutor.submit(retryTimeoutTask);
		try {
			future.get();
		} catch (Throwable ex) {
			fail(ex.toString());
		}

	}

	@Test
	public void testRetryTimeoutCallable() {
		// 每執行一次 就會快1秒，必須在3秒妹執行完
		int resultInt = 1987;
		TestRetryTimeoutCallable task = new TestRetryTimeoutCallable("Faster",
				5, resultInt);
		TimeoutCallable<Integer> timeoutTask = new TimeoutCallable<Integer>(
				task, 3, TimeUnit.SECONDS);
		RetryCallable<Integer> retryTimeoutTask = new RetryCallable<Integer>(
				timeoutTask, 4);
		Future<Integer> future = oneThreadExecutor.submit(retryTimeoutTask);
		try {
			Integer getInt = future.get();
			Assert.assertTrue(getInt.equals(resultInt));
		} catch (Throwable ex) {
			fail(ex.toString());
		}

	}

	@Test
	public void testTakeSelf() {
		long sleepMs = 750L;
		// Sleep 750ms Per Executing, and will throw NullPointerException and
		// ArithmeticException.
		// Third Executing will success and return 1.
		TestTakeSelf taskSelf = new TestTakeSelf(sleepMs);
		Callable<TestTakeSelf> task = new TakeSelfBuilder<TestTakeSelf>(
				taskSelf).timeout(1000, TimeUnit.MILLISECONDS).retry(2)
				.timeout(3000, TimeUnit.MILLISECONDS);
		try {
			Assert.assertTrue(taskSelf.getResult() == null);
			Future<TestTakeSelf> future = oneThreadExecutor.submit(task);
			future.get();
			Assert.assertTrue(taskSelf.getResult().equals(1));
		} catch (Throwable ex) {
			fail(ex.toString());
		}
	}

	private class TestTry {
		Integer i = 0;

		public Integer get() {
			try {
				return i;
			} catch (Exception e) {
				i = -1;
			} finally {
				i = 1;
			}
			return i;
		}
	}

	@Test
	public void testTry() {
		TestTry testTry = new TestTry();
		System.out.println(String.format("try get %s", testTry.get()));
		System.out.println(String.format("try get %s", testTry.i));
	}

	@Test
	public void testTimeout() {
		long sleepMs = 3000L;
		TestTakeSelf taskSelf = new TestTakeSelf(sleepMs);
		Callable<TestTakeSelf> task = new TakeSelfBuilder<TestTakeSelf>(taskSelf);
		try {
			Future<TestTakeSelf> future = in2secExecutor.submit(task);
			future.get();
		} catch (Throwable ex) {
			fail(ex.toString());
		}
		
	}
	
}
