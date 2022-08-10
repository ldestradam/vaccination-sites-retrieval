package mx.com.lestradam.covid.services;

import mx.com.lestradam.covid.entities.Coordinate;

public interface DistanceMatrixService {
	
	public void getDistanceMatrix(Coordinate coordinate);

}
