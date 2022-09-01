package mx.com.lestradam.covid.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entities.Cost;

@Repository
public interface CostRepository extends CrudRepository<Cost, Long> {
	
	Optional<Cost> findByIdPlaceFromAndIdPlaceTo(long idPlaceFrom, long idPlaceTo);

}
