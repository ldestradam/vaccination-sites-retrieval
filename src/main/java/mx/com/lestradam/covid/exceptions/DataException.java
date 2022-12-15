package mx.com.lestradam.covid.exceptions;

public class DataException  extends RuntimeException {

	public static final String MISSING_PARAMETER = "Missing parameter: "; 

	public DataException(String message) {
		super(message);
	}
	
	public DataException(String message, Throwable cause) {
		super(message, cause);
	}
	

}
