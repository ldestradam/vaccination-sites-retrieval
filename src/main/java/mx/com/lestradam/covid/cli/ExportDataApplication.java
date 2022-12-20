package mx.com.lestradam.covid.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.covid.builders.ExportParameters;
import mx.com.lestradam.covid.services.ExportDataService;

@Component
public class ExportDataApplication {
	
	private static Logger logger = LoggerFactory.getLogger(ExportDataApplication.class);
	
	@Autowired
	private ExportDataService exportSvc;
	
	public void exportData(ExportParameters params) {
		logger.info("Starting data export");
		logger.info("Export parameters: {}", params);
		if (params.getAge() != null && params.getDose() != null) {
			exportSvc.exportbyAgeAndApplication(params.getOutputEdges(), params.getOutputNodes(), params.getAge(), params.getDose());
		}
		logger.info("Data export finished");
	}

}
