package mx.com.lestradam.covid.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="places")
public class Place {
	
	@Id
	@Column(name = "idplace")
	private long id;
	
	@Column(name = "idmunicipality")
	private long idMunicipality;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "isdepot")
	private int isDepot;
	
	public Place() {}

	public Place(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdMunicipality() {
		return idMunicipality;
	}

	public void setIdMunicipality(long idMunicipality) {
		this.idMunicipality = idMunicipality;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIsDepot() {
		return isDepot;
	}

	public void setIsDepot(int isDepot) {
		this.isDepot = isDepot;
	}

	@Override
	public String toString() {
		return "Place [id=" + id + ", idMunicipality=" + idMunicipality + ", description=" + description + ", isDepot="
				+ isDepot + "]";
	}

}
