package mx.com.lestradam.covid.services;

import mx.com.lestradam.covid.entites.Coordinates;

public interface DistanceMatrixService {
	
	public void getDistanceMatrix(Coordinates coordinate);

}
