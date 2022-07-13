package mx.com.lestradam.covid.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entites.TravelCost;

@Repository
public interface TravelCostRespository extends CrudRepository<TravelCost, Long> {

}
