package mx.com.lestradam.covid.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.covid.constants.ApplicationConstant;
import mx.com.lestradam.covid.entities.Coordinate;
import mx.com.lestradam.covid.entities.Dose;
import mx.com.lestradam.covid.entities.ErrorLog;
import mx.com.lestradam.covid.entities.Municipality;
import mx.com.lestradam.covid.entities.Place;
import mx.com.lestradam.covid.exceptions.FileReaderException;
import mx.com.lestradam.covid.exceptions.RetrievalDataException;
import mx.com.lestradam.covid.repositories.CoordinateRepository;
import mx.com.lestradam.covid.repositories.DoseRepository;
import mx.com.lestradam.covid.repositories.ErrorLogRepository;
import mx.com.lestradam.covid.repositories.MunicipalityRepository;
import mx.com.lestradam.covid.repositories.PlaceRepository;

@Component
public class XlsxReader{
	
	private static final String FIRST_DOSE = "1er";
	private static final String SECOND_DOSE = "2da";
	
	private static final int DEPOT_TYPE = 1;
	private static final int PLACE_TYPE = 0;
	
	private static final int CELL_MUNICIPALITY_ID = 0;
	private static final int CELL_MUNICIPALITY_DESCRIPTION = 1;
	private static final int CELL_MUNICIPALITY_DENSITY = 2;
	private static final int CELL_MUNICIPALITY_TOTAL_POPULATION = 3;
	private static final int CELL_MUNICIPALITY_MALE_POPULATION = 4;
	private static final int CELL_MUNICIPALITY_FEMALE_POPULATION = 5;
	private static final int CELL_MUNICIPALITY_POPULATION_60 = 6;
	private static final int CELL_MUNICIPALITY_MALE_POPULATION_60 = 7;
	private static final int CELL_MUNICIPALITY_FEMALE_POPULATION_60 = 8;
	private static final int CELL_MUNICIPALITY_POPULATION_0 = 9;
	private static final int CELL_MUNICIPALITY_POPULATION_25 = 10;
	
	private static final int CELL_PLACE_MUNICIPALITY_ID = 0;
	private static final int CELL_PLACE_ID = 1;
	private static final int CELL_PLACE_DESCRIPTION = 2;
	private static final int CELL_PLACE_AGE = 3;
	private static final int CELL_PLACE_DOSE = 4;
	private static final int CELL_PLACE_FIRST_DOSE_START = 5;
	private static final int CELL_PLACE_FIRST_DOSE_END = 6;
	private static final int CELL_PLACE_SECOND_DOSE_START = 7;
	private static final int CELL_PLACE_SECOND_DOSE_END = 8;
	
	private static final int CELL_NUM_PLACE = 0;
	private static final int CELL_COORDINATES = 3;
	private static final int LAT = 1;
	private static final int LON = 2;
	
	private static final int SHEET_MUNICIPALITY = 0;
	private static final int SHEET_PLACES = 1;
	private static final int SHEET_COORDINATES = 2;
	private static final int SHEET_DEPOT = 3;
	
	private Logger logger = LoggerFactory.getLogger(XlsxReader.class);

	@Autowired
	private CoordinateRepository coordRepository;
	
	@Autowired
	private MunicipalityRepository municipalityRepo;
	
	@Autowired
	private PlaceRepository placeRepository;
	
	@Autowired
	private DoseRepository doseRepository;
	
	@Autowired
	private ErrorLogRepository errorRepository;
	
	public void retrieveDataFromXlsx(String filePath) {
		try (
			FileInputStream excelFile = new FileInputStream(new File(filePath));
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(excelFile);
		){
			Sheet municipalities = xssfWorkbook.getSheetAt(SHEET_MUNICIPALITY);
			Sheet places = xssfWorkbook.getSheetAt(SHEET_PLACES);
			Sheet coordinates = xssfWorkbook.getSheetAt(SHEET_COORDINATES);
			Sheet depots = xssfWorkbook.getSheetAt(SHEET_DEPOT);
			retrieveMunicipality(municipalities);
			retrievePlaces(places);
			retrieveCoordinates(coordinates);
			retrieveDepot(depots);
		}catch (IOException e) {
			throw new FileReaderException("Error on reading file: " + filePath, e);		
		}
	}
	
	private void retrieveMunicipality(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if(row.getRowNum() == 0)
				continue;
			try {
				extractMunicipality(row);
			} catch (RetrievalDataException  e) {
				logger.error("Error: {}", e.getMessage());
				saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
			}
		}
	}

	private void retrieveCoordinates(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if(row.getRowNum() == 0)
				continue;
			try {
				saveCoordinates(row);
			} catch (RetrievalDataException  e) {
				logger.error("Error: {}", e.getMessage());
				saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
			}
		}
	}
	
	private void retrievePlaces(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if(row.getRowNum() == 0)
				continue;
			try {
				savePlaces(row);
			} catch (RetrievalDataException  e) {
				logger.error("Error: {}", e.getMessage());
				saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
			}			
		}
	}
	
	private void retrieveDepot(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if(row.getRowNum() == 0)
				continue;
			try {
				saveDepot(row);
			} catch (RetrievalDataException  e) {
				logger.error("Error: {}", e.getMessage());
				saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
			}			
		}
	}
	
	private void saveDepot(Row row) {
		Place place = extractPlace(row, DEPOT_TYPE);
		logger.debug("Row: {} \tDepot: {}", row.getRowNum(), place.getId());
		long deleted = coordRepository.deleteByIdPlace(place.getId());
		logger.debug("Coordinates deleted: {}", deleted);
		saveCoordinates(row);
	}
	
	private void savePlaces(Row row) {		
		Place place = extractPlace(row, PLACE_TYPE);
		logger.debug("Row: {} \tPlace: {}", row.getRowNum(), place.getId());
		long deleted = coordRepository.deleteByIdPlace(place.getId());
		logger.debug("Coordinates deleted: {}", deleted);		
		extractApplicationDose(row, place.getId());
	}
	
	private void extractMunicipality(Row row) {
		long id = (long) row.getCell(CELL_MUNICIPALITY_ID).getNumericCellValue();
		Municipality temp = municipalityRepo.findById(id).orElseGet(() -> new Municipality(id));
		try {
			temp.setDescription(row.getCell(CELL_MUNICIPALITY_DESCRIPTION).getStringCellValue());
			temp.setDensity(row.getCell(CELL_MUNICIPALITY_DENSITY).getNumericCellValue());
			temp.setTotalpopulation((long) row.getCell(CELL_MUNICIPALITY_TOTAL_POPULATION).getNumericCellValue());
			temp.setMalepopulation((long) row.getCell(CELL_MUNICIPALITY_MALE_POPULATION).getNumericCellValue());
			temp.setFemalepopulation((long) row.getCell(CELL_MUNICIPALITY_FEMALE_POPULATION).getNumericCellValue());
			temp.setPopulation60((long) row.getCell(CELL_MUNICIPALITY_POPULATION_60).getNumericCellValue());
			temp.setMalepopulation60((long) row.getCell(CELL_MUNICIPALITY_MALE_POPULATION_60).getNumericCellValue());
			temp.setFemalepopulation60((long) row.getCell(CELL_MUNICIPALITY_FEMALE_POPULATION_60).getNumericCellValue());
			temp.setPopulation0((long) row.getCell(CELL_MUNICIPALITY_POPULATION_0).getNumericCellValue());
			temp.setPopulation25((long) row.getCell(CELL_MUNICIPALITY_POPULATION_25).getNumericCellValue());
			municipalityRepo.save(temp);
			logger.debug("Row: {} \tMunicipality: {}", row.getRowNum(), temp.getDescription());
		} catch (NumberFormatException | IllegalStateException  e) {
			throw new RetrievalDataException("Error getting Municipality on row " + row.getRowNum());
		}
		
	}
	
	private Place extractPlace(Row row, int type) {
		long idMunicipality = (long) row.getCell(CELL_PLACE_MUNICIPALITY_ID).getNumericCellValue();
		long idPlace = (long) row.getCell(CELL_PLACE_ID).getNumericCellValue();
		Municipality municipality = municipalityRepo.findById(idMunicipality)
				.orElseThrow(()-> new RetrievalDataException("Error getting place on row " + row.getRowNum() + ", municipality not found"));
		Optional<Place> placeOpt = placeRepository.findById(idPlace);
		Place place = null;
		if (placeOpt.isPresent()) {
			long deleted = doseRepository.deleteByIdPlace(idPlace);
			logger.debug("Dose deleted: {}", deleted);
			place = placeOpt.get();						
		}else {
			place = new Place(idPlace);
		}
		place.setIdMunicipality(municipality.getId());
		place.setDescription(row.getCell(CELL_PLACE_DESCRIPTION).getStringCellValue());
		place.setIsDepot(type);
		return placeRepository.save(place);
	}
	
	private void extractApplicationDose(Row row, long idPlace) {
		String doses = row.getCell(CELL_PLACE_DOSE).getStringCellValue();
		if(doses.contains(FIRST_DOSE) && doses.contains(SECOND_DOSE)) {
			saveDose(row, idPlace, FIRST_DOSE, CELL_PLACE_FIRST_DOSE_START, CELL_PLACE_FIRST_DOSE_END);
			saveDose(row, idPlace, SECOND_DOSE, CELL_PLACE_SECOND_DOSE_START, CELL_PLACE_SECOND_DOSE_END);
		}else if(doses.contains(FIRST_DOSE)){
			saveDose(row, idPlace, FIRST_DOSE, CELL_PLACE_FIRST_DOSE_START, CELL_PLACE_FIRST_DOSE_END);
		}else if(doses.contains(SECOND_DOSE)){
			saveDose(row, idPlace, SECOND_DOSE, CELL_PLACE_SECOND_DOSE_START, CELL_PLACE_SECOND_DOSE_END);
		}
	}
	
	private void saveDose(Row row, long idPlace, String application, int startDateIndex, int endDateIndex) {
		String age = Optional.ofNullable(row.getCell(CELL_PLACE_AGE).getStringCellValue())
				.orElseThrow(()-> new RetrievalDataException("Error getting application age on row " + row.getRowNum())) ;
		Date start = Optional.ofNullable(row.getCell(startDateIndex).getDateCellValue())
				.orElseThrow(()-> new RetrievalDataException("Error getting application start date on row " + row.getRowNum())) ;
		Date end = Optional.ofNullable(row.getCell(endDateIndex).getDateCellValue())
				.orElseThrow(()-> new RetrievalDataException("Error getting application end date on row " + row.getRowNum())) ;
		Dose dose = new Dose();
		dose.setIdPlace(idPlace);
		dose.setAge(age);
		dose.setApplication(application);
		dose.setStartDate(start);
		dose.setFinalDate(end);
		doseRepository.save(dose);
		logger.debug("Row: {} \tDose: {}", row.getRowNum(), dose);
	}
	
	private void saveCoordinates(Row row) {
		Coordinate coordinates  = extractCoordinates(row);
		Optional<Place> place = placeRepository.findById(coordinates.getIdPlace());
		if (place.isPresent()) {
			coordRepository.save(coordinates);
			logger.debug("Row: {} \tCoordinate: {}", row.getRowNum(), coordinates);
		}else {
			throw new RetrievalDataException("No place for coordinates on row: " + row.getRowNum());
		}
	}
	
	private Coordinate extractCoordinates(Row row) {				
		long idPlace = (long) row.getCell(CELL_NUM_PLACE).getNumericCellValue();
		Cell coord = Optional.ofNullable(row.getCell(CELL_COORDINATES))
				.orElseThrow(()-> new RetrievalDataException("Error getting coordinates on row " + row.getRowNum())) ;
		try {
			Map<Integer, String> latLon = getLatLongFromCoordinates(coord.getStringCellValue());
			Coordinate coordinates  = new Coordinate();
			coordinates.setIdPlace(idPlace);
			coordinates.setLatitude(latLon.get(LAT));
			coordinates.setLongitude(latLon.get(LON));
			return coordinates;
		} catch (RetrievalDataException e) {
			throw new RetrievalDataException("Error getting coordinates on row " + row.getRowNum());
		}
	}
	
	private Map<Integer, String> getLatLongFromCoordinates(String coordinates) {		
		int index = coordinates.indexOf(", ");
		if (index == -1) 
			throw new RetrievalDataException(RetrievalDataException.INVALID);
		String lat = coordinates.substring(0, index);
		String lon = coordinates.substring(index + 1, coordinates.length() - 1);
		Map<Integer, String> latLon = new HashMap<>();
		latLon.put(LAT, lat);
		latLon.put(LON, lon);
		return latLon;
	}
	
	private void saveError(String message, String type) {
		ErrorLog error = new ErrorLog();
		error.setMessage(message);
		error.setType(type);
		errorRepository.save(error);
	}

}
