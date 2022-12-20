package mx.com.lestradam.covid.services;

public interface ExportDataService {
	
	public void exportbyAgeAndApplication(String edgesFile, String nodesFile, String age, String application);
	
}
