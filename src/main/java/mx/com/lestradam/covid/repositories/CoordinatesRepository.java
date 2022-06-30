package mx.com.lestradam.covid.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entites.Coordinates;

@Repository
public interface CoordinatesRepository extends CrudRepository<Coordinates, Long> {

}
