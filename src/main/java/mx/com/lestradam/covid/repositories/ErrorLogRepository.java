package mx.com.lestradam.covid.repositories;

import org.springframework.data.repository.CrudRepository;

import mx.com.lestradam.covid.entities.ErrorLog;

public interface ErrorLogRepository extends CrudRepository<ErrorLog, Long> {

}
