package fix.java.util.concurrent;

import java.util.Date;
import java.util.concurrent.Callable;

public class TestRetryTimeoutCallable implements Callable<Integer> {
	private int sec;
	private String name;
	private int value;
	
	public TestRetryTimeoutCallable(String name, int sec,int value) {
		this.name = name;
		this.sec = sec;
		this.value =value;
	}

	@Override
	public Integer call() throws Exception {
		try {
			int sleepSec =sec;
			System.out.println(String.format("%s %s %s sleep in %s sec.",
					new Date().toString(), Thread.currentThread().toString(),
					name, sleepSec));
			Thread.sleep(sec-- * 1000);
			System.out.println(String.format("%s %s %s wake up. after %s sec. return %s.",
					new Date().toString(), Thread.currentThread().toString(),
					name, sleepSec, value));
			if (sec < 0) {
				sec = 0;
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return value;
	}
}
