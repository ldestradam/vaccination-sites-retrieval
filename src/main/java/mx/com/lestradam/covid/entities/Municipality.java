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
	
	@Column(name = "density")
	private Double density;
	
	@Column(name = "totalpopulation")
	private long totalpopulation;
	
	@Column(name = "malepopulation")
	private long malepopulation;
	
	@Column(name = "femalepopulation")
	private long femalepopulation;
	
	@Column(name = "population60")
	private long population60;
	
	@Column(name = "malepopulation60")
	private long malepopulation60;
	
	@Column(name = "femalepopulation60")
	private long femalepopulation60;
	
	@Column(name = "population0")
	private long population0;
	
	@Column(name = "population25")
	private long population25;
	
	public Municipality() {}

	public Municipality(long id) {
		this.id = id;
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

	public Double getDensity() {
		return density;
	}

	public void setDensity(Double density) {
		this.density = density;
	}

	public long getTotalpopulation() {
		return totalpopulation;
	}

	public void setTotalpopulation(long totalpopulation) {
		this.totalpopulation = totalpopulation;
	}

	public long getMalepopulation() {
		return malepopulation;
	}

	public void setMalepopulation(long malepopulation) {
		this.malepopulation = malepopulation;
	}

	public long getFemalepopulation() {
		return femalepopulation;
	}

	public void setFemalepopulation(long femalepopulation) {
		this.femalepopulation = femalepopulation;
	}

	public long getPopulation60() {
		return population60;
	}

	public void setPopulation60(long population60) {
		this.population60 = population60;
	}

	public long getMalepopulation60() {
		return malepopulation60;
	}

	public void setMalepopulation60(long malepopulation60) {
		this.malepopulation60 = malepopulation60;
	}

	public long getFemalepopulation60() {
		return femalepopulation60;
	}

	public void setFemalepopulation60(long femalepopulation60) {
		this.femalepopulation60 = femalepopulation60;
	}

	public long getPopulation0() {
		return population0;
	}

	public void setPopulation0(long population0) {
		this.population0 = population0;
	}

	public long getPopulation25() {
		return population25;
	}

	public void setPopulation25(long population25) {
		this.population25 = population25;
	}

	@Override
	public String toString() {
		return "Municipality [id=" + id + ", description=" + description + ", density=" + density + ", totalpopulation="
				+ totalpopulation + ", malepopulation=" + malepopulation + ", femalepopulation=" + femalepopulation
				+ ", population60=" + population60 + ", malepopulation60=" + malepopulation60 + ", femalepopulation60="
				+ femalepopulation60 + ", population0=" + population0 + ", population25=" + population25 + "]";
	}

}
