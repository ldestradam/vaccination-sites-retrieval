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
public class XlsxReader {

	private static final String FIRST_DOSE = "1er";
	private static final String SECOND_DOSE = "2da";

	private static final int DEPOT_TYPE = 1;
	private static final int PLACE_TYPE = 0;

	private static final int CELL_MUNICIPALITY_ID = 0;
	private static final int CELL_MUNICIPALITY_DESCRIPTION = 1;
	private static final int CELL_MUNICIPALITY_POPULATION_TOTAL = 2;
	private static final int CELL_MUNICIPALITY_POPULATION_18 = 3;
	private static final int CELL_MUNICIPALITY_POPULATION_30 = 4;
	private static final int CELL_MUNICIPALITY_POPULATION_40 = 5;
	private static final int CELL_MUNICIPALITY_POPULATION_50 = 6;
	private static final int CELL_MUNICIPALITY_POPULATION_60 = 7;

	private static final int CELL_DOSE_PLACE_ID = 0;
	private static final int CELL_DOSE_AGE = 1;
	private static final int CELL_DOSE_APPLICATION = 2;
	private static final int CELL_DOSE_FIRST_APPLICATION_START = 3;
	private static final int CELL_DOSE_FIRST_APPLICATION_END = 4;
	private static final int CELL_DOSE_SECOND_APPLICATION_START = 5;
	private static final int CELL_DOSE_SECOND_APPLICATION_END = 6;

	private static final int CELL_PLACE_ID = 0;
	private static final int CELL_PLACE_MUNICIPALITY_ID = 1;
	private static final int CELL_PLACE_DESCRIPTION = 2;
	private static final int CELL_PLACE_COORDINATES = 3;

	private static final int LAT = 1;
	private static final int LON = 2;

	private static final int SHEET_MUNICIPALITY = 0;
	private static final int SHEET_COORDINATES = 1;
	private static final int SHEET_PLACES = 2;
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
		deleteCoordinatesAndDose();
		try (FileInputStream excelFile = new FileInputStream(new File(filePath));
				XSSFWorkbook xssfWorkbook = new XSSFWorkbook(excelFile);) {
			Sheet municipalities = xssfWorkbook.getSheetAt(SHEET_MUNICIPALITY);
			Sheet places = xssfWorkbook.getSheetAt(SHEET_PLACES);
			Sheet coordinates = xssfWorkbook.getSheetAt(SHEET_COORDINATES);
			Sheet depots = xssfWorkbook.getSheetAt(SHEET_DEPOT);
			retrieveMunicipality(municipalities);
			retrieveCoordinates(coordinates);
			retrieveDoseApplication(places);
			retrieveDepot(depots);
		} catch (IOException e) {
			throw new FileReaderException("Error on reading file: " + filePath, e);
		}
	}

	public void deleteCoordinatesAndDose() {
		coordRepository.deleteAll();
		doseRepository.deleteAll();
	}

	private void retrieveMunicipality(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if (row.getRowNum() == 0)
				continue;
			try {
				Municipality municipality = extractMunicipality(row);
				logger.trace("Row: {} \tMunicipality: {}", row.getRowNum(), municipality);
			} catch (RetrievalDataException e) {
				logger.error(e.getMessage());
				saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
			}
		}
	}

	private void retrieveCoordinates(Sheet sheet) {
		logger.debug("Retrieving coordinates ...");
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if (row.getRowNum() == 0)
				continue;
			try {
				Place place = extractPlace(row, PLACE_TYPE);
				Coordinate coordinate = extractCoordinates(row, place.getId());
				logger.trace("Row: {} \t Application site: {} \t Coords: {}", row.getRowNum(), place, coordinate);
			} catch (RetrievalDataException e) {
				logger.error(e.getMessage());
				saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
			}
		}
	}

	private void retrieveDoseApplication(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if (row.getRowNum() == 0)
				continue;
			try {
				extractApplicationDose(row);
			} catch (RetrievalDataException e) {
				logger.error(e.getMessage());
				saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
			}
		}
	}

	private void retrieveDepot(Sheet sheet) {
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();
			if (row.getRowNum() == 0)
				continue;
			try {
				Place place = extractPlace(row, DEPOT_TYPE);
				Coordinate coordinate = extractCoordinates(row, place.getId());
				logger.trace("Row: {} \t Application depot: {} \t Coords: {}", row.getRowNum(), place, coordinate);
			} catch (RetrievalDataException e) {
				logger.error(e.getMessage());
				saveError(e.getMessage(), ApplicationConstant.ERROR_TYPE_DATA);
			}
		}
	}

	private Municipality extractMunicipality(Row row) {
		long id = (long) row.getCell(CELL_MUNICIPALITY_ID).getNumericCellValue();
		Municipality temp = municipalityRepo.findById(id).orElseGet(() -> new Municipality(id));
		try {
			temp.setDescription(row.getCell(CELL_MUNICIPALITY_DESCRIPTION).getStringCellValue());
			temp.setTotalpopulation((long) row.getCell(CELL_MUNICIPALITY_POPULATION_TOTAL).getNumericCellValue());
			temp.setPopulation18((long) row.getCell(CELL_MUNICIPALITY_POPULATION_18).getNumericCellValue());
			temp.setPopulation30((long) row.getCell(CELL_MUNICIPALITY_POPULATION_30).getNumericCellValue());
			temp.setPopulation40((long) row.getCell(CELL_MUNICIPALITY_POPULATION_40).getNumericCellValue());
			temp.setPopulation50((long) row.getCell(CELL_MUNICIPALITY_POPULATION_50).getNumericCellValue());
			temp.setPopulation60((long) row.getCell(CELL_MUNICIPALITY_POPULATION_60).getNumericCellValue());
			return municipalityRepo.save(temp);
		} catch (NumberFormatException | IllegalStateException e) {
			throw new RetrievalDataException("Error getting Municipality on row " + row.getRowNum());
		}

	}

	private Place extractPlace(Row row, int type) {
		long idMunicipality = (long) row.getCell(CELL_PLACE_MUNICIPALITY_ID).getNumericCellValue();
		long idPlace = (long) row.getCell(CELL_PLACE_ID).getNumericCellValue();
		Municipality municipality = municipalityRepo.findById(idMunicipality)
				.orElseThrow(() -> new RetrievalDataException(
						"Error getting place on row " + row.getRowNum() + ", municipality not found"));
		Place place = placeRepository.findById(idPlace).orElse(new Place(idPlace));
		place.setIdMunicipality(municipality.getId());
		place.setDescription(row.getCell(CELL_PLACE_DESCRIPTION).getStringCellValue());
		place.setIsDepot(type);
		return placeRepository.save(place);
	}

	private void extractApplicationDose(Row row) {
		long idPlace = (long) row.getCell(CELL_DOSE_PLACE_ID).getNumericCellValue();
		Place place = placeRepository.findById(idPlace).orElseThrow(
				() -> new RetrievalDataException("Error getting dose on row " + row.getRowNum() + ", place not found"));
		String doses = row.getCell(CELL_DOSE_APPLICATION).getStringCellValue();
		if (doses.contains(FIRST_DOSE) && doses.contains(SECOND_DOSE)) {
			saveDose(row, place.getId(), FIRST_DOSE, CELL_DOSE_FIRST_APPLICATION_START,
					CELL_DOSE_FIRST_APPLICATION_END);
			saveDose(row, place.getId(), SECOND_DOSE, CELL_DOSE_SECOND_APPLICATION_START,
					CELL_DOSE_SECOND_APPLICATION_END);
		} else if (doses.contains(FIRST_DOSE)) {
			saveDose(row, place.getId(), FIRST_DOSE, CELL_DOSE_FIRST_APPLICATION_START,
					CELL_DOSE_FIRST_APPLICATION_END);
		} else if (doses.contains(SECOND_DOSE)) {
			saveDose(row, place.getId(), SECOND_DOSE, CELL_DOSE_SECOND_APPLICATION_START,
					CELL_DOSE_SECOND_APPLICATION_END);
		} else {
			throw new RetrievalDataException(
					"Error getting dose on row " + row.getRowNum() + ", dose application not found");
		}
	}

	private void saveDose(Row row, long idPlace, String application, int startDateIndex, int endDateIndex) {
		String age = Optional.ofNullable(row.getCell(CELL_DOSE_AGE).getStringCellValue()).orElseThrow(
				() -> new RetrievalDataException("Error getting dose application age on row " + row.getRowNum()));
		Date start = Optional.ofNullable(row.getCell(startDateIndex).getDateCellValue())
				.orElseThrow(() -> new RetrievalDataException(
						"Error getting dose application start date on row " + row.getRowNum()));
		Date end = Optional.ofNullable(row.getCell(endDateIndex).getDateCellValue()).orElseThrow(
				() -> new RetrievalDataException("Error getting dose application end date on row " + row.getRowNum()));
		Dose dose = new Dose();
		dose.setIdPlace(idPlace);
		dose.setAge(age);
		dose.setApplication(application);
		dose.setStartDate(start);
		dose.setFinalDate(end);
		dose.setQuantity(0L);
		dose = doseRepository.save(dose);
		logger.trace("Row: {} \t Application dose: {}", row.getRowNum(), dose);
	}

	private Coordinate extractCoordinates(Row row, long idPlace) {
		Cell coord = Optional.ofNullable(row.getCell(CELL_PLACE_COORDINATES))
				.orElseThrow(() -> new RetrievalDataException("Error getting coordinates on row " + row.getRowNum()));
		try {
			Map<Integer, String> latLon = getLatLongFromCoordinates(coord.getStringCellValue());
			Coordinate coordinates = new Coordinate();
			coordinates.setIdPlace(idPlace);
			coordinates.setLatitude(latLon.get(LAT));
			coordinates.setLongitude(latLon.get(LON));
			return coordRepository.save(coordinates);
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
