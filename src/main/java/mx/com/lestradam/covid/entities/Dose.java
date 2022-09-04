package mx.com.lestradam.covid.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="doses")
public class Dose {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "iddose")
	private long id;
	
	@Column(name = "idplace")
	private long idPlace;
	
	@Column(name = "age")
	private String age;
	
	@Column(name = "application")
	private String application;
	
	@Column(name = "startdate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column(name = "finaldate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date finalDate;
	
	@Column(name = "quantity")
	private long quantity;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdPlace() {
		return idPlace;
	}

	public void setIdPlace(long idPlace) {
		this.idPlace = idPlace;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Dose [id=" + id + ", idPlace=" + idPlace + ", age=" + age + ", application=" + application
				+ ", startDate=" + startDate + ", finalDate=" + finalDate + ", quantity=" + quantity + "]";
	}

}
