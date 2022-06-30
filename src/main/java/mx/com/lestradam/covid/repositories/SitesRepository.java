package mx.com.lestradam.covid.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entites.Site;

@Repository
public interface SitesRepository extends CrudRepository<Site, Long> {

}
