package mx.com.lestradam.covid.entites;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SITES")
public class Site {

	@Id
	private long id;
	private String description;
	private String coordinates;
	
	public Site() {}

	public Site(long id, String description, String coordinates) {
		this.id = id;
		this.description = description;
		this.coordinates = coordinates;
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
	
	public String getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public String toString() {
		return "Site [id=" + id + ", description=" + description + ", coordinates=" + coordinates + "]";
	}
	
}
