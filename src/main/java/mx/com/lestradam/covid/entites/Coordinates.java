package mx.com.lestradam.covid.entites;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="COORDINATES")
public class Coordinates {
	
	@Id
	private long id;
	private String description;
	private String latitude;
	private String longitude;
	private String status;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;
	
	@PrePersist
	public void prePersist() {
		this.createAt = new Date();
		this.updateAt = new Date();
	}
	
	@PreUpdate
	public void preUpdate() {
		this.updateAt = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Coordinates [id=" + id + ", description=" + description + ", latitude=" + latitude + ", longitude="
				+ longitude + ", status=" + status + ", createAt=" + createAt + ", updateAt=" + updateAt + "]";
	}
	
}
