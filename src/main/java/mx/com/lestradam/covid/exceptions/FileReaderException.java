package mx.com.lestradam.covid.exceptions;

public class FileReaderException extends RuntimeException{
	
	private static final long serialVersionUID = -5388474822426984854L;
		
	public FileReaderException(String message) {
		super(message);
	}
	
	public FileReaderException(String message, Throwable ex) {
		super(message, ex);
	}

}
