package mx.com.lestradam.covid.dto;

public class CoordinatesDTO {
	
	private long id;	
	private String latitude;
	private String longitude;
	
	public CoordinatesDTO() {
	}

	public CoordinatesDTO(long id, String latitude, String longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "CoordinatesDTO [id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
