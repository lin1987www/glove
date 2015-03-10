package fix.java.util.concurrent;

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
