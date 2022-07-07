package mx.com.lestradam.covid.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.springframework.stereotype.Service;

import mx.com.lestradam.covid.entites.Coordinates;
import mx.com.lestradam.covid.exceptions.RetrievalCoordinatesException;
import mx.com.lestradam.covid.repositories.CoordinatesRepository;

@Service
public class XlsxReaderServiceImpl implements XlsxReaderService {
	
	private static final int ID_CELL = 0;
	private static final int SITE_CELL = 2;
	private static final int COORDINATES_CELL = 3;
	private static final int LAT = 1;
	private static final int LON = 2;
	private static final int COORDINATES_SHEET = 0;
	
	private Logger logger = LoggerFactory.getLogger(XlsxReaderServiceImpl.class);

	@Autowired
	private CoordinatesRepository coordRepository;

	@Override
	public void retrieveDatafromXlsx(String filePath) {
		logger.info("File directory: {}", filePath);
		try (
				FileInputStream excelFile = new FileInputStream(new File(filePath));
				XSSFWorkbook xssfWorkbook = new XSSFWorkbook(excelFile);
			){
			Sheet coordinateSheet = xssfWorkbook.getSheetAt(COORDINATES_SHEET);
			Iterator<Row> rows = coordinateSheet.iterator();
			while (rows.hasNext()) {
				Row row = rows.next();
				if(row.getRowNum() == 0)
					continue;
				saveCoordinates(row);
			}
		}catch (IOException e) {
			logger.error("Error on reading coordinates: {}", e.getMessage());
		}
	}
	
	private void saveCoordinates(Row row) {
		try {
			Coordinates coordinates  = extractCoordinates(row);
			coordinates = coordRepository.save(coordinates);
			logger.debug("Vaccination coordinates: {}", coordinates);
		} catch (RetrievalCoordinatesException ex) {
			logger.error("Error getting coordinates on row {}", row.getRowNum());
			logger.error("Exception: {}", ex.getMessage());
		}
	}
	
	private Coordinates extractCoordinates(Row row) {
		Iterator<Cell> cells = row.iterator();
		Coordinates coordinates  = new Coordinates();
		while (cells.hasNext()) {
			Cell cell = cells.next();
			int index = cell.getColumnIndex();
			switch (index) {
				case ID_CELL:
					Double id = cell.getNumericCellValue();
					coordinates.setId( id.longValue() );
					break;
				case COORDINATES_CELL:
					Map<Integer, String> latLon = getLatLongFromCoordinates(cell.getStringCellValue());
					coordinates.setLatitude(latLon.get(LAT));
					coordinates.setLongitude(latLon.get(LON));
					break;
				case SITE_CELL:
					coordinates.setDescription(cell.getStringCellValue());
					break;
				default:
					break;
			}
		}
		return coordinates;
	}
	
	private Map<Integer, String> getLatLongFromCoordinates(String coordinates) {
		Optional<String> optCoodinate = Optional.ofNullable(coordinates);
		if (!optCoodinate.isPresent() || coordinates.isBlank()) 
			throw new RetrievalCoordinatesException(RetrievalCoordinatesException.EMPTY);
		int index = coordinates.indexOf(", ");
		if (index == -1) 
			throw new RetrievalCoordinatesException(RetrievalCoordinatesException.INVALID);
		String lat = coordinates.substring(0, index);
		String lon = coordinates.substring(index + 1, coordinates.length() - 1);
		Map<Integer, String> latLon = new HashMap<>();
		latLon.put(LAT, lat);
		latLon.put(LON, lon);
		return latLon;
	}

}
