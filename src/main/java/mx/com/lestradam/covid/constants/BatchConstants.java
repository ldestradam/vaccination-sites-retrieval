package mx.com.lestradam.covid.constants;

public class BatchConstants {
	
	public static final int CHUNK_SIZE_COORDINATES = 5;
	public static final int CHUNK_SIZE_TRAVEL_COST = 5;
	public static final String SUCCESS_STATUS = "SUCCESS";
	public static final String FAILED_STATUS = "FAILED";
	public static final String INITIAL_STATUS = "NOT STARTED";
	
	
	private BatchConstants() {
		throw new IllegalStateException("Constants class");
	}

}
