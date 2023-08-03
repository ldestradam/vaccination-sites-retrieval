package mx.com.lestradam.covid.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.maps.errors.ApiException;

import mx.com.lestradam.covid.constants.ApplicationConstant;
import mx.com.lestradam.covid.entities.Coordinate;
import mx.com.lestradam.covid.entities.ErrorLog;
import mx.com.lestradam.covid.exceptions.DistanceMatrixException;
import mx.com.lestradam.covid.repositories.ErrorLogRepository;
import mx.com.lestradam.covid.services.DistanceMatrixService;
import mx.com.lestradam.covid.utils.CommonUtils;

public class CoordinatesItemProcessor implements ItemProcessor<Coordinate, Coordinate>{
	
	private Logger logger = LoggerFactory.getLogger(CoordinatesItemProcessor.class);
	
	@Autowired
	private DistanceMatrixService distanceSvc;
	
	@Autowired
	private ErrorLogRepository errorRepository;

	@Override
	public Coordinate process(Coordinate item) throws Exception {
		try {
			distanceSvc.getDistanceMatrix(item);
		} catch (DistanceMatrixException e) {
			logger.error("Error getting distance matrix from coordinates: {}", item);
			logger.error("Message: {}", e.getMessage());
			if (CommonUtils.isCausedBy(e, ApiException.class))
				logger.error("Google Maps Service: {}", e.getCause().getMessage());
			saveError(e.getMessage() + " Place: " + item.getIdPlace(), ApplicationConstant.ERROR_TYPE_API);
		} catch (Exception e) {
			logger.error("General exception on processing coordinates: {}", item);
			logger.error("Message: {}", e.getMessage());
			saveError(e.getMessage() + " Place: " + item.getIdPlace(), ApplicationConstant.ERROR_TYPE_API);
		}
		return item;
	}
	
	private void saveError(String message, String type) {
		ErrorLog error = new ErrorLog();
		error.setMessage(message);
		error.setType(type);
		errorRepository.save(error);
	}

}
