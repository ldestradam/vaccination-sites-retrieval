package mx.com.lestradam.covid.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.com.lestradam.covid.entites.Site;
import mx.com.lestradam.covid.repositories.SitesRepository;

@Service
public class XlsxReaderImpl implements IXlsxReader {
	
	private static final int ID_CELL = 0;
	private static final int SITE_CELL = 2;
	private static final int  COORDINATES_CELL = 3;
	
	private Logger logger = LoggerFactory.getLogger(XlsxReaderImpl.class);
	
	@Autowired
	private SitesRepository sitesRepository;

	@Override
	public void retrieveDatafromXlsx(String filePath) {
		logger.info("File directory: {}", filePath);
		try (
				FileInputStream excelFile = new FileInputStream(new File(filePath));
				XSSFWorkbook xssfWorkbook = new XSSFWorkbook(excelFile);
			){			
			Sheet coordinateSheet = xssfWorkbook.getSheetAt(0);
	        Iterator<Row> rows = coordinateSheet.iterator();
	        while (rows.hasNext()) {
	        	Row row = rows.next();
	        	if(row.getRowNum() == 0)
	        		continue;
                Iterator<Cell> cells = row.iterator();
                Site site = new Site();
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    int index = cell.getColumnIndex();
                    switch (index) {
						case ID_CELL:
							Double id = cell.getNumericCellValue();
							site.setId( id.longValue() );
							break;
						case COORDINATES_CELL:
							site.setCoordinates(cell.getStringCellValue());
							break;
						case SITE_CELL:
							site.setDescription(cell.getStringCellValue());
							break;
						default:
							break;
					}
                }	
                site = sitesRepository.save(site);
                logger.debug("Vaccination sites: {}", site);
	        }
		} catch (IOException e) {
			logger.error("Error on reading coordinates: {}", e.getMessage());
			e.printStackTrace();
		}       
	}

}
