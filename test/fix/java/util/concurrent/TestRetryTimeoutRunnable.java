package fix.java.util.concurrent;

import java.util.Date;

public class TestRetryTimeoutRunnable implements Runnable {
	private int sec;
	private String name;

	public TestRetryTimeoutRunnable(String name, int sec) {
		this.name = name;
		this.sec = sec;
	}

	@Override
	public void run() {
		try {
			int sleepSec =sec;
			System.out.println(String.format("%s %s %s sleep in %s sec.",
					new Date().toString(), Thread.currentThread().toString(),
					name, sleepSec));
			Thread.sleep(sec-- * 1000);
			System.out.println(String.format("%s %s %s wake up. after %s sec.",
					new Date().toString(), Thread.currentThread().toString(),
					name, sleepSec));
			if (sec < 0) {
				sec = 0;
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
