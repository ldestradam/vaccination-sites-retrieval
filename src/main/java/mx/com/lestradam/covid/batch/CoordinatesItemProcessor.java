package mx.com.lestradam.covid.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import mx.com.lestradam.covid.dto.CoordinatesDTO;
import mx.com.lestradam.covid.dto.SedeDTO;

public class CoordinatesItemProcessor implements ItemProcessor<SedeDTO, CoordinatesDTO>{
	
	private Logger logger = LoggerFactory.getLogger(CoordinatesItemProcessor.class);

	@Override
	public CoordinatesDTO process(SedeDTO item) throws Exception {
		int index = item.getCoordinates().indexOf(", ");
		String lat = item.getCoordinates().substring(0, index);
		String lon = item.getCoordinates().substring(index + 1, item.getCoordinates().length() - 1);
		CoordinatesDTO coordinate = new CoordinatesDTO(item.getId(), lat, lon); 
		logger.info("Coordenada: {}", coordinate);
		return coordinate;
	}

}
