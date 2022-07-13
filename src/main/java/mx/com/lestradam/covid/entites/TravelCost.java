package mx.com.lestradam.covid.entites;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="TRAVEL_COST")
public class TravelCost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long idSiteFrom;
	private long idSiteTo;
	private String distance;
	private String duration;
	private String status;
	
	@Column(name = "create_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;
	
	@PrePersist
	public void prePersist() {
		this.createAt = new Date();
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

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

	@Override
	public String toString() {
		return "TravelCostDTO [idSiteFrom=" + idSiteFrom + ", idSiteTo=" + idSiteTo + ", distance=" + distance
				+ ", duration=" + duration + ", status=" + status + "]";
	}
}
