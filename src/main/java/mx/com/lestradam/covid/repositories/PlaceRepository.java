package mx.com.lestradam.covid.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entities.Place;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Long> {
	
	List<Place> findByIsDepot(int isDepot);
}
