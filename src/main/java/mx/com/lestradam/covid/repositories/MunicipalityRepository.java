package mx.com.lestradam.covid.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entities.Municipality;

@Repository
public interface MunicipalityRepository extends CrudRepository<Municipality, Long>{

}
