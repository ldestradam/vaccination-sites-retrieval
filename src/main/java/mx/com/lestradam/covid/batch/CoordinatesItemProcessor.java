package mx.com.lestradam.covid.batch;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import mx.com.lestradam.covid.RetrievalCoordinatesException;
import mx.com.lestradam.covid.dto.CoordinatesDTO;
import mx.com.lestradam.covid.entites.Site;

public class CoordinatesItemProcessor implements ItemProcessor<Site, CoordinatesDTO>{
	
	private Logger logger = LoggerFactory.getLogger(CoordinatesItemProcessor.class);

	@Override
	public CoordinatesDTO process(Site item) throws Exception {
		CoordinatesDTO coordinate = null;
		try {
			coordinate = getCoordinateFromSite(item);
		} catch (RetrievalCoordinatesException ex) {
			logger.error("Exception: {}", ex.getMessage());
			logger.error("Error getting coordinates from site: {}", item);
		}
		return coordinate;
	}
	
	private CoordinatesDTO getCoordinateFromSite(Site site) {
		CoordinatesDTO coordinate = new CoordinatesDTO();
		Optional<String> optCoodinate = Optional.ofNullable(site.getCoordinates());
		if (!optCoodinate.isPresent() || site.getCoordinates().isBlank()) 
			throw new RetrievalCoordinatesException(RetrievalCoordinatesException.EMPTY);
		int index = site.getCoordinates().indexOf(", ");
		if (index == -1) 
			throw new RetrievalCoordinatesException(RetrievalCoordinatesException.INVALID);
		String lat = site.getCoordinates().substring(0, index);
		String lon = site.getCoordinates().substring(index + 1, site.getCoordinates().length() - 1);
		coordinate.setId(site.getId());
		coordinate.setLatitude(lat);
		coordinate.setLongitude(lon);
		return coordinate;
	}

}
