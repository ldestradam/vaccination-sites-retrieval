package mx.com.lestradam.covid.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="costs")
public class Cost {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idcost")
	private long id;
	
	@Column(name = "idplacefrom")
	private long idPlaceFrom;
	
	@Column(name = "idplaceto")
	private long idPlaceTo;
	
	@Column(name = "distance")
	private String distance;
	
	@Column(name = "duration")
	private String duration;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "destination_address")
	private String destination;
	
	@Column(name = "origin_address")
	private String origin;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdPlaceFrom() {
		return idPlaceFrom;
	}

	public void setIdPlaceFrom(long idPlaceFrom) {
		this.idPlaceFrom = idPlaceFrom;
	}

	public long getIdPlaceTo() {
		return idPlaceTo;
	}

	public void setIdPlaceTo(long idPlaceTo) {
		this.idPlaceTo = idPlaceTo;
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

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	public String toString() {
		return "Cost [id=" + id + ", idPlaceFrom=" + idPlaceFrom + ", idPlaceTo=" + idPlaceTo + ", distance=" + distance
				+ ", duration=" + duration + ", status=" + status + ", destination=" + destination + ", origin="
				+ origin + "]";
	}

}
