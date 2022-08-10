package mx.com.lestradam.covid.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="municipalities")
public class Municipality {
	
	@Id
	@Column(name = "idmunicipality")
	private long id;
	
	@Column(name = "description")
	private String description;

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

	@Override
	public String toString() {
		return "Municipality [id=" + id + ", description=" + description + "]";
	}

}
