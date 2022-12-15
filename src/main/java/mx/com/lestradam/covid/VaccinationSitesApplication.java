package mx.com.lestradam.covid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import mx.com.lestradam.covid.cli.CommandLineApplication;
import mx.com.lestradam.covid.exceptions.DataException;
import mx.com.lestradam.covid.exceptions.FileReaderException;
import mx.com.lestradam.covid.exceptions.GeneralBatchExecption;

@SpringBootApplication
@EnableEncryptableProperties
public class VaccinationSitesApplication implements CommandLineRunner{
	
	private static Logger logger = LoggerFactory.getLogger(VaccinationSitesApplication.class);
	
	@Autowired
	private CommandLineApplication cli;

	public static void main(String[] args) {
		SpringApplication.run(VaccinationSitesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			cli.execute(args);
		} catch (FileReaderException | DataException | GeneralBatchExecption e) {
			logger.error("Error: {}", e.getMessage());
			e.printStackTrace();
		}
	}
}
