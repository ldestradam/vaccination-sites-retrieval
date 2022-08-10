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
	
	private static final int CELL_MUNICIPALITY_ID = 0;
	private static final int CELL_MUNICIPALITY_DESCRIPTION = 1;
	private static final int CELL_PLACE_ID = 2;
	private static final int CELL_PLACE_DESCRIPTION = 3;
	private static final int CELL_AGE = 4;
	private static final int CELL_DOSE = 5;
	private static final int CELL_FIRST_DOSE_START = 6;
	private static final int CELL_FIRST_DOSE_END = 7;
	private static final int CELL_SECOND_DOSE_START = 8;
	private static final int CELL_SECOND_DOSE_END = 9;
	
	
	private static final int CELL_NUM_PLACE = 0;
	private static final int CELL_COORDINATES = 3;
	private static final int LAT = 1;
	private static final int LON = 2;
	private static final int SHEET_PLACES = 0;
	private static final int COORDINATES_SHEET = 1;
	
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
			Sheet placeSheet = xssfWorkbook.getSheetAt(SHEET_PLACES);
			Sheet coordinatesSheet = xssfWorkbook.getSheetAt(COORDINATES_SHEET);
			retrievePlaces(placeSheet);
			retrieveCoordinates(coordinatesSheet);
		}catch (IOException e) {
			throw new FileReaderException("Error on reading file: " + filePath, e);		
		}
	}

	private void retrieveCoordinates(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if(row.getRowNum() == 0)
				continue;
			saveCoordinates(row);
		}
	}
	
	
	
	private void retrievePlaces(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if(row.getRowNum() == 0)
				continue;
			savePlaces(row);
		}
	}
	
	private void savePlaces(Row row) {
		Municipality municipality = extractMunicipality(row);
		Place place = extractPlace(row, municipality.getId());
		extractApplicationDose(row, place.getId());
	}
	
	private Municipality extractMunicipality(Row row) {
		long id = (long) row.getCell(CELL_MUNICIPALITY_ID).getNumericCellValue();		
		return municipalityRepo.findById(id).orElseGet(()->{
			Municipality temp = new Municipality();
			temp.setId(id);
			temp.setDescription(row.getCell(CELL_MUNICIPALITY_DESCRIPTION).getStringCellValue());
			return municipalityRepo.save(temp);
		});
	}
	
	private Place extractPlace(Row row, long idMunicipality) {
		long id = (long) row.getCell(CELL_PLACE_ID).getNumericCellValue();
		return placeRepository.findById(id).orElseGet(()->{
			Place tmp = new Place();
			tmp.setId(id);
			tmp.setIdMunicipality(idMunicipality);
			tmp.setDescription(row.getCell(CELL_PLACE_DESCRIPTION).getStringCellValue());
			return placeRepository.save(tmp);
		});
	}
	
	private void extractApplicationDose(Row row, long idPlace) {
		try {
			String doses = row.getCell(CELL_DOSE).getStringCellValue();
			if(doses.contains(FIRST_DOSE) && doses.contains(SECOND_DOSE)) {
				saveDose(row, idPlace, FIRST_DOSE, CELL_FIRST_DOSE_START, CELL_FIRST_DOSE_END);
				saveDose(row, idPlace, SECOND_DOSE, CELL_SECOND_DOSE_START, CELL_SECOND_DOSE_END);
			}else if(doses.contains(FIRST_DOSE)){
				saveDose(row, idPlace, FIRST_DOSE, CELL_FIRST_DOSE_START, CELL_FIRST_DOSE_END);
			}else if(doses.contains(SECOND_DOSE)){
				saveDose(row, idPlace, SECOND_DOSE, CELL_SECOND_DOSE_START, CELL_SECOND_DOSE_END);
			}
		} catch (RetrievalDataException  e) {
			saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
		}
	}
	
	
	
	private void saveDose(Row row, long idPlace, String application, int startDateIndex, int endDateIndex) {
		String age = Optional.ofNullable(row.getCell(CELL_AGE).getStringCellValue())
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
	}
	
	private void saveCoordinates(Row row) {
		try {
			Coordinate coordinates  = extractCoordinates(row);
			Optional<Place> place = placeRepository.findById(coordinates.getIdPlace());
			if (place.isPresent()) {
				coordinates = coordRepository.save(coordinates);
				logger.debug("Vaccination coordinates: {}", coordinates);
			}else {
				saveError("No place for coordinates on row: " + row.getRowNum(), ApplicationConstant.ERROR_TYPE_DATA);
			}			
		} catch (RetrievalDataException e) {
			saveError( e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
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
