package mx.com.lestradam.covid;

public class RetrievalCoordinatesException extends RuntimeException {
	
	private static final long serialVersionUID = 5669645051361885838L;
	public static final String EMPTY = "Empty coordinate values.";
	public static final String INVALID = "Invalid coordinate values.";
	
	
	public RetrievalCoordinatesException(String message) {
		super(message);
	}
	
	public RetrievalCoordinatesException(String message, Throwable cause) {
		super(message, cause);
	}
	

}
