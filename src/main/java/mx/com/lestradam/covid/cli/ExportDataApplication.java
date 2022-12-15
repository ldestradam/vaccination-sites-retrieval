package mx.com.lestradam.covid.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import mx.com.lestradam.covid.builders.ExportParameters;
import mx.com.lestradam.covid.services.TravelCostService;

@Component
public class ExportDataApplication {
	
	private static Logger logger = LoggerFactory.getLogger(ExportDataApplication.class);
	
	private TravelCostService travelCostSvc;
	
	public void exportData(ExportParameters params) {
		logger.info("Starting data export");
		if (params.getAge() != null && params.getDose() != null) {
			travelCostSvc.getTravelCostbyAgeAndApplication(params.getOutputEdges(), params.getOutputNodes(), params.getAge(), params.getDose());
		}
		logger.info("Data export finished");
	}

}
