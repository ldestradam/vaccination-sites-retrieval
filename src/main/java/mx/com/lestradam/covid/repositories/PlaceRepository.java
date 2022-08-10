package mx.com.lestradam.covid.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entities.Place;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Long> {

}