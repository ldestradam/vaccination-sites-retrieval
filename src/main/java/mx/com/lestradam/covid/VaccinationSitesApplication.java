package mx.com.lestradam.covid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class VaccinationSitesApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaccinationSitesApplication.class, args);
	}
	
}
