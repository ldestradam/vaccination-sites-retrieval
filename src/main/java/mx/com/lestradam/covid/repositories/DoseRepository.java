package mx.com.lestradam.covid.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entities.Dose;

@Repository
public interface DoseRepository extends CrudRepository<Dose, Long> {
	
	List<Dose> findByAgeLikeAndApplicationLike(String age, String application);
	List<Dose> findByIdGreaterThanAndAgeLikeAndApplicationLike(long id, String age, String application);
	long deleteByIdPlace(long idPlace);

}
