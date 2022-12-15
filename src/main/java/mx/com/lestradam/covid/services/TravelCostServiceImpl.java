package mx.com.lestradam.covid.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;

import mx.com.lestradam.covid.entities.Cost;
import mx.com.lestradam.covid.entities.Dose;
import mx.com.lestradam.covid.entities.Place;
import mx.com.lestradam.covid.exceptions.FileReaderException;
import mx.com.lestradam.covid.repositories.CostRepository;
import mx.com.lestradam.covid.repositories.DoseRepository;
import mx.com.lestradam.covid.repositories.PlaceRepository;

@Service
public class TravelCostServiceImpl implements TravelCostService{
	
	private Logger logger = LoggerFactory.getLogger(TravelCostServiceImpl.class);
	private static final String[] HEADER_EDGES = {"Source", "Target", "Weight", "Type"};
	private static final String[] HEADER_NODES = {"Id", "Label","Quantity"};
	
	@Autowired
	private CostRepository costRespository;
	
	@Autowired
	private DoseRepository doseRepository;
	
	@Autowired
	private PlaceRepository placeRepository;

	@Override
	public void getTravelCostbyAgeAndApplication(String edgesFile, String nodesFile, String age, String application) {
		try (CSVWriter edges = new CSVWriter(new FileWriter(edgesFile, true));
			CSVWriter nodes = new CSVWriter(new FileWriter(nodesFile, true))){
			edges.writeNext(HEADER_EDGES);
			nodes.writeNext(HEADER_NODES);
			List<Dose> dosesFrom = doseRepository.findByAgeLikeAndApplicationLike(age, application);
			List<Place> depots = placeRepository.findByIsDepot(1);
			logger.info("Total place: {}", dosesFrom.size());
			for (Dose doseFrom : dosesFrom) {
				logger.debug("From: {}", doseFrom);
				String[] node = {String.valueOf(doseFrom.getIdPlace()), "Sede " + doseFrom.getIdPlace(), Long.toString(doseFrom.getQuantity())};
				nodes.writeNext(node);
				List<Dose> dosesTo = doseRepository.findByIdGreaterThanAndAgeLikeAndApplicationLike(doseFrom.getId(), age, application);
				for(Dose doseTo : dosesTo) {
					logger.debug("To: {}", doseTo);
					Optional<Cost> costOpt = costRespository.findByIdPlaceFromAndIdPlaceTo(doseFrom.getIdPlace(), doseTo.getIdPlace());
					if(costOpt.isPresent()) {
						Cost cost = costOpt.get();
						logger.debug("Cost: {}", cost);
						String[] edge = {String.valueOf(cost.getIdPlaceFrom()), String.valueOf(cost.getIdPlaceTo()), String.valueOf(cost.getDistance()), "undirected"};
						edges.writeNext(edge);
					}
				}
				for(Place depot: depots) {
					Optional<Cost> costOpt = costRespository.findByIdPlaceFromAndIdPlaceTo(doseFrom.getIdPlace(), depot.getId());
					if(costOpt.isPresent()) {
						Cost cost = costOpt.get();
						logger.debug("Cost Depot: {}", cost);
						String[] edge = {String.valueOf(cost.getIdPlaceFrom()), String.valueOf(cost.getIdPlaceTo()), String.valueOf(cost.getDistance()), "undirected"};
						edges.writeNext(edge);
					}
				}
			}
			
		} catch (IOException e) {
			throw new FileReaderException("Error reading node and/or edge files...", e);
		}
	}

}
