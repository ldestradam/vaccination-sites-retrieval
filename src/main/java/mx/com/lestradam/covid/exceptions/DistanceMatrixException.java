package mx.com.lestradam.covid.exceptions;

public class DistanceMatrixException extends RuntimeException{

	private static final long serialVersionUID = -6763596870172233766L;
	public static final String PARAMS_ENCODEFAILED = "Parameters encode failed.";
	public static final String PARAMS_LIMITFAILED = "Parameter encoding exceeded character limit.";
	public static final String REQUEST_FAILED = "Client request failed.";
	
	public DistanceMatrixException(String message) {
		super(message);
	}
	
	public DistanceMatrixException(String message, Throwable cause) {
		super(message, cause);
	}

}
