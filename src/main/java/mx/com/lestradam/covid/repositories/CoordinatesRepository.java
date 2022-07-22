package mx.com.lestradam.covid.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entites.Coordinates;

@Repository
public interface CoordinatesRepository extends PagingAndSortingRepository<Coordinates, Long> {

	public Page<Coordinates> findByIdGreaterThan(long id, Pageable pageable);
	public long countByStatus(String status);
	
}
