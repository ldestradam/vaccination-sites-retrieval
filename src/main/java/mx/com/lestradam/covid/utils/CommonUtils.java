package mx.com.lestradam.covid.utils;

public class CommonUtils {
	
	private CommonUtils() {}
	
	public static boolean isCausedBy(Throwable caught, Class<? extends Throwable> isCausedBy) {
		if (caught == null) 
			return false;
		else if (isCausedBy.isAssignableFrom(caught.getClass())) 
			return true;
		else 
			return isCausedBy(caught.getCause(), isCausedBy);
	}

}
