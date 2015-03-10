package fix.java.util.concurrent;

import static org.junit.Assert.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Tests {

	ScheduledExecutorFix oneThreadExecutor;
	ScheduledExecutorFix twoThreadExecutor;

	@Before
	public void setUp() throws Exception {
		oneThreadExecutor = new ScheduledExecutorFix(1);
		twoThreadExecutor = new ScheduledExecutorFix(2);
	}

	@After
	public void tearDown() throws Exception {
		oneThreadExecutor.shutdown();
		twoThreadExecutor.shutdown();
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
		TimeoutRunnable timeoutTask = new TimeoutRunnable(task,
				timeoutMs, TimeUnit.MILLISECONDS);
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
		TimeoutRunnable timeoutTask = new TimeoutRunnable(task,
				timeoutMs, TimeUnit.MILLISECONDS);
		TimeoutRunnable timeoutTask2 =new TimeoutRunnable(task2,
				timeoutMs, TimeUnit.MILLISECONDS);
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
		TimeoutRunnable timeoutTask = new TimeoutRunnable(task,
				timeoutMs, TimeUnit.MILLISECONDS);
		TimeoutRunnable timeoutTask2 = new TimeoutRunnable(task2,
				timeoutMs, TimeUnit.MILLISECONDS);
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
		TestRetryTimeoutRunnable task = new TestRetryTimeoutRunnable("Faster",5);
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
		TestRetryTimeoutCallable task = new TestRetryTimeoutCallable("Faster",5,resultInt);
		TimeoutCallable<Integer> timeoutTask = new TimeoutCallable<Integer>(task, 3, TimeUnit.SECONDS);
		RetryCallable<Integer> retryTimeoutTask = new RetryCallable<Integer>(timeoutTask, 4);
		Future<Integer> future = oneThreadExecutor.submit(retryTimeoutTask);
		try {
			Integer getInt = future.get();
			Assert.assertTrue(getInt.equals(resultInt));
		} catch (Throwable ex) {
			fail(ex.toString());
		}

	}
}
