package mx.com.lestradam.covid.exceptions;

public class RetrievalDataException extends RuntimeException {
	
	private static final long serialVersionUID = 5669645051361885838L;
	public static final String EMPTY = "Empty coordinate values.";
	public static final String INVALID = "Invalid coordinate values.";	
	
	
	public RetrievalDataException(String message) {
		super(message);
	}
	
	public RetrievalDataException(String message, Throwable cause) {
		super(message, cause);
	}
	

}
