package fix.java.util.concurrent;

public class TestRetryRunnable implements Runnable {

	private int successTimes = 0;
	
	public int throwTimes = 0 ;
	
	public TestRetryRunnable(int successTimes){
		this.successTimes = successTimes;
	}
	
	@Override
	public void run() {
		if(throwTimes == successTimes){
			return;
		}else{
			throwTimes = throwTimes + 1;
			throw new RuntimeException(String.format("TestRetryRunnable %s in %s Times.", throwTimes, successTimes));
		}
	}
}
