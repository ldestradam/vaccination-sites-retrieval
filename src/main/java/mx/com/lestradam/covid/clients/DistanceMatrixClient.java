package mx.com.lestradam.covid.clients;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;

import mx.com.lestradam.covid.config.GoogleApiKey;
import mx.com.lestradam.covid.exceptions.DistanceMatrixException;

@Component
public class DistanceMatrixClient {
	
	@Autowired
	private GoogleApiKey apiKey;
	
	public DistanceMatrix getTravelDistanceAndTime(String origins, String destinations) {
		DistanceMatrix matrix = null;
		try {
			matrix  = DistanceMatrixApi.getDistanceMatrix(apiKey.getApiContext(), new String[] {destinations}, new String[] {origins}).await();		
		} catch (ApiException | IOException ex) {
			throw new DistanceMatrixException(DistanceMatrixException.REQUEST_FAILED, ex);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return matrix;
	}

}
