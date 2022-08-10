package mx.com.lestradam.covid.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.LatLng;

import mx.com.lestradam.covid.clients.DistanceMatrixClient;
import mx.com.lestradam.covid.entities.Coordinate;
import mx.com.lestradam.covid.entities.Cost;
import mx.com.lestradam.covid.repositories.CoordinateRepository;
import mx.com.lestradam.covid.repositories.CostRespository;
import mx.com.lestradam.covid.utils.DistanceMatrixParamsEncoder;

@Service
public class DistanceMatrixServiceImpl implements DistanceMatrixService{
	
	private Logger logger = LoggerFactory.getLogger(DistanceMatrixServiceImpl.class);
	private static final int CHUNK_DESTINATIONS = 10; 
	
	@Autowired
	private DistanceMatrixClient client;
	
	@Autowired
	private CoordinateRepository coordRepository;
	
	@Autowired
	private CostRespository costRepository;

	@Override
	public void getDistanceMatrix(Coordinate coordinates) {
		long idOrigin = coordinates.getId();
		logger.debug("Origin: {}",coordinates);
		String queryOrigins = getQueryLocations(Arrays.asList(coordinates));
		Pageable pageable = PageRequest.of(0, CHUNK_DESTINATIONS);
		int totalPages = coordRepository.findByIdGreaterThan(idOrigin, pageable).getTotalPages();
		for (int i = 0; i < totalPages ; i++) {
			pageable = PageRequest.of(i, CHUNK_DESTINATIONS);
			Page<Coordinate> chunk = coordRepository.findByIdGreaterThan(idOrigin, pageable);
			List<Coordinate> destinations = chunk.getContent();
			String queryDestinations = getQueryLocations(destinations);
			DistanceMatrix matrix = client.getTravelDistanceAndTime(queryOrigins, queryDestinations );
			DistanceMatrixRow[] rows = matrix.rows;
			for (int j = 0; j < rows.length; j++) {
				DistanceMatrixElement element = rows[j].elements[0];
				Cost cost = new Cost();
				cost.setIdPlaceFrom(coordinates.getIdPlace());
				cost.setIdPlaceTo(destinations.get(j).getIdPlace());
				cost.setDistance(String.valueOf(element.distance.inMeters));
				cost.setDuration(String.valueOf(element.duration.inSeconds));
				cost.setStatus(element.status.toString());
				cost = costRepository.save(cost);
				logger.debug("Travel Costs: {}", cost);				
			}
		}
	}
	
	
	
	private String getQueryLocations(List<Coordinate> locations) {
		List<LatLng> latLngdestinations = locations.stream()
			.map( coordinates -> new LatLng( Double.valueOf(coordinates.getLatitude()), Double.valueOf(coordinates.getLongitude())))
			.collect(Collectors.toList());
		return DistanceMatrixParamsEncoder.generateLocationEncoding(latLngdestinations);
	}
	
}
