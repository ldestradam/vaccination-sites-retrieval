package mx.com.lestradam.covid.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
	
	@Value("${google.maps.api-key}")
	private String apikey; 
	
	@Bean(destroyMethod = "destroy")
	public GoogleApiKey apikey() {
		return new GoogleApiKey(apikey); 
	}
	
}
