package fix.java.util.concurrent;

import java.util.Date;
import java.util.concurrent.Callable;

public class TestTimeoutCallable implements Callable<Integer> {

	private long ms;
	private String name;
	private int value;

	public TestTimeoutCallable(String name, long ms, int value) {
		this.name = name;
		this.ms = ms;
		this.value = value;
	}

	@Override
	public Integer call() throws Exception {
		try {
			System.out.println(String.format("%s %s %s sleep.",
					new Date().toString(), Thread.currentThread().toString(),
					name));
			Thread.sleep(ms);
			System.out.println(String.format("%s %s %s wake up. return %s.",
					new Date().toString(), Thread.currentThread().toString(),
					name, value));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return value;
	}
}
