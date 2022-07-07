package mx.com.lestradam.covid.dto;

public class TravelCostDTO {

	private long idSiteFrom;
	private long idSiteTo;
	private String distance;
	private String duration;
	private String status;
	
	public long getIdSiteFrom() {
		return idSiteFrom;
	}
	
	public void setIdSiteFrom(long idSiteFrom) {
		this.idSiteFrom = idSiteFrom;
	}
	
	public long getIdSiteTo() {
		return idSiteTo;
	}
	
	public void setIdSiteTo(long idSiteTo) {
		this.idSiteTo = idSiteTo;
	}
	
	public String getDistance() {
		return distance;
	}
	
	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public String getDuration() {
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
