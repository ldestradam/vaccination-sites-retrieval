package mx.com.lestradam.covid.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entities.Dose;

@Repository
public interface DoseRepository extends CrudRepository<Dose, Long> {
	
	List<Dose> findByAgeLikeAndApplicationLike(String age, String application);
	long countByAgeLikeAndApplicationLike(String age, String application);
	List<Dose> findByIdGreaterThanAndAgeLikeAndApplicationLike(long id, String age, String application);
	
	@Query("select count(*) from Dose dose "
			+ "where dose.idPlace in (select place.id from Place place where place.idMunicipality = :idMunicipality) "
			+ "and dose.age like :age and dose.application like :application")
	long countPlacesPerMunicipalityAndAgeAndApplication(@Param("idMunicipality") long idMunicipality, @Param("age") String age, @Param("application") String application);

}
