package mx.com.lestradam.covid.batch;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.lestradam.covid.entities.Dose;
import mx.com.lestradam.covid.entities.Municipality;
import mx.com.lestradam.covid.repositories.DoseRepository;
import mx.com.lestradam.covid.repositories.MunicipalityRepository;

public class DosesItemProcessor implements ItemProcessor<Dose, Dose>{
	
	private Logger logger = LoggerFactory.getLogger(DosesItemProcessor.class);
	
	private static final String AGE_60 = "60";
	private static final String AGE_50 = "50";
	private static final String AGE_40 = "40";
	private static final String AGE_30 = "30";
	private static final String AGE_18 = "18";
	
	@Autowired
	private DoseRepository doseRepository;
	
	@Autowired
	private MunicipalityRepository municipalityRepository;  

	@Override
	public Dose process(Dose item) throws Exception {
		logger.debug("Actual dose : {}",item);
		Optional<Municipality> municipalityOpt =  municipalityRepository.findByDoseAndPlace(item.getIdPlace(), item.getId());
		String application = "%" + item.getApplication() + "%";
		String age = "%" + item.getAge() + "%";
		if (municipalityOpt.isPresent()) {
			Municipality municipality =  municipalityOpt.get();
			long numOfDoses = doseRepository.countPlacesPerMunicipalityAndAgeAndApplication(municipality.getId(), age, application);
			logger.debug("Number of dose: {}", numOfDoses);
			long quantity = getQuantity(item, municipality, numOfDoses);
			item.setQuantity(quantity);
		}
		logger.debug("Expected dose: {}",item);
		return item;
	}
	
	private long getQuantity(Dose dose, Municipality municipality, long numOfDoses) {
		long quantity = 0;
		if (dose.getAge().contains(AGE_60))
			quantity = municipality.getPopulation60() / numOfDoses;
		return quantity;
	}

}
