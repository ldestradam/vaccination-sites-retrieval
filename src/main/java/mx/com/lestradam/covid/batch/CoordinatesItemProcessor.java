package mx.com.lestradam.covid.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.maps.errors.ApiException;

import mx.com.lestradam.covid.entites.Coordinates;
import mx.com.lestradam.covid.exceptions.DistanceMatrixException;
import mx.com.lestradam.covid.services.DistanceMatrixService;
import mx.com.lestradam.covid.utils.CommonUtils;

public class CoordinatesItemProcessor implements ItemProcessor<Coordinates, Coordinates>{
	
	private Logger logger = LoggerFactory.getLogger(CoordinatesItemProcessor.class);
	
	@Autowired
	private DistanceMatrixService distanceSvc;

	@Override
	public Coordinates process(Coordinates item) throws Exception {
		try {
			distanceSvc.getDistanceMatrix(item);
		} catch (DistanceMatrixException e) {
			logger.error("Error getting distance matrix from coordinates: {}", item);
			logger.error("Message: {}", e.getMessage());
			if (CommonUtils.isCausedBy(e, ApiException.class)) {
				logger.error("Google Maps Service: {}", e.getCause().getMessage());
			}
		} catch (Exception e) {
			logger.error("General exception on processing coordinates: {}", item);
			logger.error("Cause: {}", e.getMessage());
		}		
		return item;
	}

}
