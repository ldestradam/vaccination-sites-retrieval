package mx.com.lestradam.covid.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import mx.com.lestradam.covid.exceptions.DistanceMatrixException;

public class DistanceMatrixParamsEncoder {
	
	private static final int CHARACTER_LIMIT = 8192; 
	private static final String PREFIX = "enc:";
	private static final String SUFFIX = ":";
	
	private DistanceMatrixParamsEncoder() {}
	
	public static String encodeQueryParams(Map<String, String> params) {
		String encodedURL = params.keySet().stream()
				.map( param -> {
					try {
						return param + "=" + encodeValue(params.get(param));
					} catch (UnsupportedEncodingException ex) {
						throw new DistanceMatrixException(DistanceMatrixException.PARAMS_ENCODEFAILED, ex);
					}
				})
				.collect(Collectors.joining("&"));
		if(encodedURL.length() > CHARACTER_LIMIT)
			throw new DistanceMatrixException(DistanceMatrixException.PARAMS_LIMITFAILED);
		return encodedURL;
	}
	
	public static String generateLocationEncoding(List<LatLng> locations) {
		EncodedPolyline encodedLocations = new EncodedPolyline(locations);
		return PREFIX + encodedLocations.getEncodedPath() + SUFFIX;
	}
	
	private static String encodeValue(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
	}

}
