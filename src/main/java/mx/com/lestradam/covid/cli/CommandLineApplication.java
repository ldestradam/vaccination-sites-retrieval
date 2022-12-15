package mx.com.lestradam.covid.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.covid.builders.ExportParameters;
import mx.com.lestradam.covid.builders.ExportParameters.ExportParametersBuilder;
import mx.com.lestradam.covid.exceptions.DataException;


@Component
public class CommandLineApplication {
	
	private static Logger logger = LoggerFactory.getLogger(CommandLineApplication.class);
	
	private static final String OPEARATION_KEY = "operation";
	private static final String OPEARATION_BATCH = "batch";
	private static final String OPEARATION_EXPORT = "export";
	private static final String BATCH_INPUT_FILE = "batch.input";
	private static final String EXPORT_NODE_FILE = "export.output.nodes";
	private static final String EXPORT_EDGE_FILE = "export.output.edges";
	private static final String EXPORT_AGE = "export.search.age";
	private static final String EXPORT_DOSE = "export.search.dose";
	
	private static final String SEPARATOR = ":";
	private String[] arguments;
	
	@Autowired
	private BatchApplication batchApp;
	
	@Autowired
	private ExportDataApplication exportApp;
	
	public void execute(String[] arguments) {
		this.arguments = arguments;
		if (!checkArgumentKey(OPEARATION_KEY)) 
			throw new DataException(DataException.MISSING_PARAMETER + OPEARATION_KEY);
		String algo = retrieveArgumentValue(OPEARATION_KEY);
		if (algo.equals(OPEARATION_BATCH)) {
			logger.info("Checking batch parameters...");
			if (!checkArgumentKey(BATCH_INPUT_FILE)) {
				throw new DataException(DataException.MISSING_PARAMETER + BATCH_INPUT_FILE);
			}
			String inputFile = retrieveArgumentValue(BATCH_INPUT_FILE);
			batchApp.execute(inputFile);
		} else if(algo.equals(OPEARATION_EXPORT)){
			logger.info("Checking export parameters...");
			if (!checkArgumentKey(EXPORT_NODE_FILE)) {
				throw new DataException(DataException.MISSING_PARAMETER + EXPORT_NODE_FILE);
			}
			if (!checkArgumentKey(EXPORT_EDGE_FILE)) {
				throw new DataException(DataException.MISSING_PARAMETER + EXPORT_EDGE_FILE);
			}
			String outputNodes = retrieveArgumentValue(EXPORT_NODE_FILE);
			String outputEdges = retrieveArgumentValue(EXPORT_EDGE_FILE);
			ExportParametersBuilder builder = new ExportParameters.ExportParametersBuilder(outputNodes, outputEdges);
			if (checkArgumentKey(EXPORT_AGE))
				builder.setAge(retrieveArgumentValue(EXPORT_AGE));
			if (checkArgumentKey(EXPORT_DOSE))
				builder.setDose(retrieveArgumentValue(EXPORT_DOSE));
			ExportParameters params = builder.build();
			exportApp.exportData(params);
		} else {
			throw new DataException("Invalid value for parameter: " + OPEARATION_KEY + ", possible values [" + OPEARATION_BATCH + "|" + OPEARATION_EXPORT + "]");
		}
	}
	
	private boolean checkArgumentKey(String key) {
		for(int i = 0; i < arguments.length; i++) {
			if(arguments[i].indexOf(key) == 0)
				return true;
		}
		return false;
	}
	
	private String retrieveArgumentValue(String key) {
		for(int i = 0; i < arguments.length; i++) {
			if(arguments[i].indexOf(key) == 0) {
				String arg = arguments[i];
				return arg.substring(arg.indexOf(SEPARATOR) + 1, arg.length());
			}
		}
		throw new DataException("Missing value for parameter: " + key);
	}

}
