package mx.com.lestradam.covid.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ExportDataServiceImpl implements ExportDataService{
	
	private Logger logger = LoggerFactory.getLogger(ExportDataServiceImpl.class);
	private static final String[] HEADER_EDGES = {"Source", "Target", "Weight", "Type"};
	private static final String[] HEADER_NODES = {"Id", "Label", "Quantity", "Longitude", "Latitude"};
	
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
		List<String[]> edges = new ArrayList<>();
		List<String[]> nodes = new ArrayList<>();
		edges.add(HEADER_EDGES);
		nodes.add(HEADER_NODES);
		List<Dose> dosesFrom = doseRepository.findByAgeLikeAndApplicationLike(age, application);
		List<Place> depots = placeRepository.findByIsDepot(1);
		logger.info("Total place: {}", dosesFrom.size());
		for (Dose doseFrom : dosesFrom) {
			logger.debug("From: {}", doseFrom);
			Optional<Coordinate> coordsOpt = coordRepository.findByIdPlace(doseFrom.getIdPlace());
			String[] node;
			String id = String.valueOf(doseFrom.getIdPlace());
			String label = "Sede " + doseFrom.getIdPlace();
			String quantity = Long.toString(doseFrom.getQuantity());
			if (coordsOpt.isPresent()) {
				Coordinate coord = coordsOpt.get();
				node = new String[]{id, label, quantity, coord.getLongitude(), coord.getLatitude()};
			} else {
				node = new String[]{id, label, quantity, "N/A", "N/A"};
			}
			nodes.add(node);
			List<Dose> dosesTo = doseRepository.findByIdGreaterThanAndAgeLikeAndApplicationLike(doseFrom.getId(), age, application);
			for(Dose doseTo : dosesTo) {
				logger.debug("To: {}", doseTo);
				Optional<Cost> costOpt = costRespository.findByIdPlaceFromAndIdPlaceTo(doseFrom.getIdPlace(), doseTo.getIdPlace());
				if(costOpt.isPresent()) {
					Cost cost = costOpt.get();
					logger.debug("Cost: {}", cost);
					String[] edge = {String.valueOf(cost.getIdPlaceFrom()), String.valueOf(cost.getIdPlaceTo()), String.valueOf(cost.getDistance()), "undirected"};
					edges.add(edge);
				}
			}
			for(Place depot: depots) {
				Optional<Cost> costOpt = costRespository.findByIdPlaceFromAndIdPlaceTo(doseFrom.getIdPlace(), depot.getId());
				if(costOpt.isPresent()) {
					Cost cost = costOpt.get();
					logger.debug("Cost Depot: {}", cost);
					String[] edge = {String.valueOf(cost.getIdPlaceFrom()), String.valueOf(cost.getIdPlaceTo()), String.valueOf(cost.getDistance()), "undirected"};
					edges.add(edge);
				}
			}
		}
		CsvWriter.createFile(nodesFile, nodes);
		CsvWriter.createFile(edgesFile, edges);
	}

}
