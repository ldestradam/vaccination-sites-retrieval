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
import mx.com.lestradam.covid.entites.Coordinates;
import mx.com.lestradam.covid.entites.TravelCost;
import mx.com.lestradam.covid.repositories.CoordinatesRepository;
import mx.com.lestradam.covid.repositories.TravelCostRespository;
import mx.com.lestradam.covid.utils.DistanceMatrixParamsEncoder;

@Service
public class DistanceMatrixServiceImpl implements DistanceMatrixService{
	
	private Logger logger = LoggerFactory.getLogger(DistanceMatrixServiceImpl.class);
	private static final int CHUNK_DESTINATIONS = 10; 
	
	@Autowired
	private DistanceMatrixClient client;
	
	@Autowired
	private CoordinatesRepository coordRepository;
	
	@Autowired
	private TravelCostRespository travelRepository;

	@Override
	public void getDistanceMatrix(Coordinates coordinates) {
		long idOrigin = coordinates.getId();
		logger.debug("Origin: {}",coordinates);
		String queryOrigins = getQueryLocations(Arrays.asList(coordinates));
		Pageable pageable = PageRequest.of(0, CHUNK_DESTINATIONS);
		int totalPages = coordRepository.findByIdGreaterThan(idOrigin, pageable).getTotalPages();
		for (int i = 0; i < totalPages ; i++) {
			pageable = PageRequest.of(i, CHUNK_DESTINATIONS);
			Page<Coordinates> chunk = coordRepository.findByIdGreaterThan(idOrigin, pageable);
			List<Coordinates> destinations = chunk.getContent();
			String queryDestinations = getQueryLocations(destinations);
			DistanceMatrix matrix = client.getTravelDistanceAndTime(queryOrigins, queryDestinations );
			DistanceMatrixRow[] rows = matrix.rows;
			for (int j = 0; j < rows.length; j++) {
				DistanceMatrixElement element = rows[j].elements[0];
				TravelCost cost = new TravelCost();
				cost.setIdSiteFrom(idOrigin);
				cost.setIdSiteTo(destinations.get(j).getId());
				cost.setDistance(String.valueOf(element.distance.inMeters));
				cost.setDuration(String.valueOf(element.duration.inSeconds));
				cost.setStatus(element.status.toString());
				logger.debug("Travel Costs: {}", cost);
				travelRepository.save(cost);
			}
		}
	}
	
	
	
	private String getQueryLocations(List<Coordinates> locations) {
		List<LatLng> latLngdestinations = locations.stream()
			.map( coordinates -> new LatLng( Double.valueOf(coordinates.getLatitude()), Double.valueOf(coordinates.getLongitude())))
			.collect(Collectors.toList());
		return DistanceMatrixParamsEncoder.generateLocationEncoding(latLngdestinations);
	}
	
}
