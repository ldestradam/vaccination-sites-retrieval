package mx.com.lestradam.covid.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entities.Municipality;

@Repository
public interface MunicipalityRepository extends CrudRepository<Municipality, Long>{
	
	@Query("select municipality from Dose dose "
			+ "inner join Place place on dose.idPlace = place.id "
			+ "inner join Municipality municipality on place.idMunicipality = municipality.id  "
			+ "where dose.idPlace = :idPlace and dose.id = :idDose")
	public Optional<Municipality> findByDoseAndPlace(@Param("idPlace") long idPlace, @Param("idDose") long idDose);

}
