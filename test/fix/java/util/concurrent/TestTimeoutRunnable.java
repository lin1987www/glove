package fix.java.util.concurrent;

import java.util.Date;

public class TestTimeoutRunnable implements Runnable {

	private long ms;
	private String name;

	public TestTimeoutRunnable(String name, long ms) {
		this.name = name;
		this.ms = ms;
	}

	@Override
	public void run() {
		try {
			System.out.println(String.format("%s %s %s sleep.",
					new Date().toString(), Thread.currentThread().toString(),
					name));
			Thread.sleep(ms);
			System.out.println(String.format("%s %s %s wake up.",
					new Date().toString(), Thread.currentThread().toString(),
					name));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
