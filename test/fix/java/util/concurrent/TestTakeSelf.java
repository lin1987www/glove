package fix.java.util.concurrent;

public class TestTakeSelf extends TakeSelf<Integer> {

	private Integer x = 1;
	private Integer y = null;
	private long sleepMs = 0;
	
	public TestTakeSelf(long sleepMs){
		this.sleepMs = sleepMs;
	}
	
	@Override
	public Integer take() throws Exception {
		Thread.sleep(sleepMs);
		int result = x/y;
		return result;
	}

	@Override
	public boolean handleException(Throwable ex) {
		if(ex instanceof NullPointerException){
			y = 0;
		}if(ex instanceof  java.lang.ArithmeticException){
			y = 1;
		}
		return false;
	}

}
