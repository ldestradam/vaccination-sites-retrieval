package mx.com.lestradam.covid.exceptions;

public class GeneralBatchExecption extends RuntimeException{

	private static final long serialVersionUID = -4782273645346900762L;

	public GeneralBatchExecption(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneralBatchExecption(String message) {
		super(message);
	}

}
