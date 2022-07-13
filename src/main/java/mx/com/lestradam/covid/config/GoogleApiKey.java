package mx.com.lestradam.covid.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.maps.GeoApiContext;

public class GoogleApiKey {
	
	private Logger logger = LoggerFactory.getLogger(GoogleApiKey.class);
	
	private GeoApiContext apiContext;
		
	public GoogleApiKey(String apikey) {
		apiContext = new GeoApiContext.Builder().apiKey(apikey).build();
	}
	
	public GeoApiContext getApiContext() {
		return apiContext;
	}

	public void destroy() {
		logger.info("Shutting down google maps api context...");
		apiContext.shutdown();
	}
}
