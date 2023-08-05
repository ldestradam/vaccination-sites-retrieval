package mx.com.lestradam.covid.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.com.lestradam.covid.entities.Coordinate;
import mx.com.lestradam.covid.entities.Cost;
import mx.com.lestradam.covid.entities.Dose;
import mx.com.lestradam.covid.entities.Place;
import mx.com.lestradam.covid.repositories.CoordinateRepository;
import mx.com.lestradam.covid.repositories.CostRepository;
import mx.com.lestradam.covid.repositories.DoseRepository;
import mx.com.lestradam.covid.repositories.PlaceRepository;
import mx.com.lestradam.covid.utils.CsvWriter;

@Service
public class ExportDataServiceImpl implements ExportDataService {

	private Logger logger = LoggerFactory.getLogger(ExportDataServiceImpl.class);
	private static final String[] HEADER_EDGES = { "Source", "Target", "Weight", "Type" };
	private static final String[] HEADER_NODES = { "Id", "Label", "Quantity", "Longitude", "Latitude" };
	private static final int PAGE_SIZE = 50;

	@Autowired
	private CostRepository costRespository;

	@Autowired
	private DoseRepository doseRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private CoordinateRepository coordRepository;

	@Override
	public void exportbyAgeAndApplication(String edgesFile, String nodesFile, String age, String application) {
		logger.info("Getting nodes and egdes for age: {} and application: {}", age, application);
		List<String[]> edges = new ArrayList<>();
		List<String[]> nodes = new ArrayList<>();
		edges.add(HEADER_EDGES);
		nodes.add(HEADER_NODES);
		List<Dose> dosesFrom = doseRepository.findByAgeLikeAndApplicationLike(age, application);
		List<Place> depots = placeRepository.findByIsDepot(1);
		List<Long> placesId = dosesFrom.stream().map(Dose::getIdPlace).collect(Collectors.toList());
		logger.info("Getting nodes - Places: {} - Depots: {}", placesId.size(), depots.size());
		// Depots' nodes
		for (Place depot : depots) {
			placesId.add(depot.getId());
			String[] node = getNodeDetail(depot);
			nodes.add(node);
		}
		// Nodes
		for (Dose doseFrom : dosesFrom) {
			String[] node = getNodeDetail(doseFrom);
			nodes.add(node);
		}
		CsvWriter.createFile(nodesFile, nodes);
		// Edges
		Pageable pageable = PageRequest.of(0, PAGE_SIZE);
		Page<Cost> firstPage = costRespository.findByIdPlaceFromInAndIdPlaceToIn(placesId, placesId, pageable);
		int totalPages = firstPage.getTotalPages();
		logger.info("Getting edges - Total: {} - Pages: {}", firstPage.getTotalElements(), totalPages);
		for (int page = 0; page < totalPages; page++) {
			pageable = PageRequest.of(page, PAGE_SIZE);
			List<Cost> costs = costRespository.findByIdPlaceFromInAndIdPlaceToIn(placesId, placesId, pageable)
					.getContent();
			for (Cost cost : costs) {
				String[] edge = getEdgeDetail(cost);
				edges.add(edge);
			}
		}
		CsvWriter.createFile(edgesFile, edges);
	}

	private String[] getNodeDetail(Place place) {
		String id = String.valueOf(place.getId());
		String label = "Deposito " + place.getId();
		Optional<Coordinate> coordsOpt = coordRepository.findByIdPlace(place.getId());
		if (coordsOpt.isPresent()) {
			Coordinate coord = coordsOpt.get();
			return new String[] { id, label, "0", coord.getLongitude(), coord.getLatitude() };
		}
		return new String[] { id, label, "0", "N/A", "N/A" };
	}

	private String[] getNodeDetail(Dose dose) {
		String id = String.valueOf(dose.getIdPlace());
		String label = "Sede " + dose.getIdPlace();
		String quantity = Long.toString(dose.getQuantity());
		Optional<Coordinate> coordsOpt = coordRepository.findByIdPlace(dose.getIdPlace());
		if (coordsOpt.isPresent()) {
			Coordinate coord = coordsOpt.get();
			return new String[] { id, label, quantity, coord.getLongitude(), coord.getLatitude() };
		}
		return new String[] { id, label, quantity, "N/A", "N/A" };
	}

	private String[] getEdgeDetail(Cost cost) {
		return new String[] { String.valueOf(cost.getIdPlaceFrom()), String.valueOf(cost.getIdPlaceTo()),
				String.valueOf(cost.getDistance()), "undirected" };
	}

}
