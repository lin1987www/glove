package fix.java.util.concurrent;

public class TestReRunnable implements IReRunnable {
	private Integer x = 1;
	private Integer y = null;

	@Override
	public boolean handleException(Throwable ex) {
		if(ex instanceof NullPointerException){
			y = 0;
			System.out.println("y is null! y be set 0.");
			return true;
		}if(ex instanceof  java.lang.ArithmeticException){
			y = 1;
			System.out.println("y is 0! y be set 1.");
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		int result = x/y;
	}
}
