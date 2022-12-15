package mx.com.lestradam.covid.services;

public interface TravelCostService {
	
	public void getTravelCostbyAgeAndApplication(String edgesFile, String nodesFile, String age, String application);
	
}
