package mx.com.lestradam.covid.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "municipalities")
public class Municipality {

	@Id
	@Column(name = "idmunicipality")
	private long id;

	@Column(name = "description")
	private String description;

	@Column(name = "population18")
	private long population18;

	@Column(name = "population30")
	private long population30;

	@Column(name = "population40")
	private long population40;

	@Column(name = "population50")
	private long population50;

	@Column(name = "population60")
	private long population60;

	@Column(name = "totalpopulation")
	private long totalpopulation;

	public Municipality() {
	}

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

	public long getPopulation18() {
		return population18;
	}

	public void setPopulation18(long population18) {
		this.population18 = population18;
	}

	public long getPopulation30() {
		return population30;
	}

	public void setPopulation30(long population30) {
		this.population30 = population30;
	}

	public long getPopulation40() {
		return population40;
	}

	public void setPopulation40(long population40) {
		this.population40 = population40;
	}

	public long getPopulation50() {
		return population50;
	}

	public void setPopulation50(long population50) {
		this.population50 = population50;
	}

	public long getPopulation60() {
		return population60;
	}

	public void setPopulation60(long population60) {
		this.population60 = population60;
	}

	public long getTotalpopulation() {
		return totalpopulation;
	}

	public void setTotalpopulation(long totalpopulation) {
		this.totalpopulation = totalpopulation;
	}

	@Override
	public String toString() {
		return "Municipality [id=" + id + ", description=" + description + ", population18=" + population18
				+ ", population30=" + population30 + ", population40=" + population40 + ", population50=" + population50
				+ ", population60=" + population60 + ", totalpopulation=" + totalpopulation + "]";
	}

}
