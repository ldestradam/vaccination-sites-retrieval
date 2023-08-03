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
import mx.com.lestradam.covid.repositories.CostRepository;
import mx.com.lestradam.covid.utils.DistanceMatrixParamsEncoder;

@Service
public class DistanceMatrixServiceImpl implements DistanceMatrixService {

	private Logger logger = LoggerFactory.getLogger(DistanceMatrixServiceImpl.class);
	private static final int CHUNK_DESTINATIONS = 10;

	@Autowired
	private DistanceMatrixClient client;

	@Autowired
	private CoordinateRepository coordRepository;

	@Autowired
	private CostRepository costRepository;

	@Override
	public void getDistanceMatrix(Coordinate origin) {
		logger.debug("Getting costs for origin: {}", origin);
		String queryOrigins = getQueryLocations(Arrays.asList(origin));
		Pageable pageable = PageRequest.of(0, CHUNK_DESTINATIONS);
		int totalPages = coordRepository.findByIdPlaceNot(origin.getIdPlace(), pageable).getTotalPages();
		for (int page = 0; page < totalPages; page++) {
			pageable = PageRequest.of(page, CHUNK_DESTINATIONS);
			Page<Coordinate> chunk = coordRepository.findByIdPlaceNot(origin.getIdPlace(), pageable);
			List<Coordinate> destinations = chunk.getContent();
			if (logger.isTraceEnabled()) {
				for (int j = 0; j < destinations.size(); j++)
					logger.trace("Page: {} Destination: {}", page, destinations.get(j));
			}
			String queryDestinations = getQueryLocations(destinations);
			DistanceMatrix matrix = client.getTravelDistanceAndTime(queryDestinations, queryOrigins);
			DistanceMatrixRow[] origins = matrix.rows;
			for (int j = 0; j < origins.length; j++) {
				DistanceMatrixRow row = origins[j];
				DistanceMatrixElement[] dests = row.elements;
				for (int k = 0; k < dests.length; k++) {
					DistanceMatrixElement dest = dests[k];
					Cost cost = new Cost();
					cost.setIdPlaceFrom(origin.getIdPlace());
					cost.setIdPlaceTo(destinations.get(k).getIdPlace());
					cost.setDestination(matrix.destinationAddresses[k]);
					cost.setOrigin(matrix.originAddresses[j]);
					cost.setDistance(String.valueOf(dest.distance.inMeters));
					cost.setDuration(String.valueOf(dest.duration.inSeconds));
					cost.setStatus(dest.status.toString()); 
					cost = costRepository.save(cost);
					logger.debug("Travel Costs: {}", cost);
				}
			}
		}
	}

	private String getQueryLocations(List<Coordinate> locations) {
		List<LatLng> latLngdestinations = locations.stream()
				.map(coordinates -> new LatLng(Double.valueOf(coordinates.getLatitude()),
						Double.valueOf(coordinates.getLongitude())))
				.collect(Collectors.toList());
		return DistanceMatrixParamsEncoder.generateLocationEncoding(latLngdestinations);
	}

}
