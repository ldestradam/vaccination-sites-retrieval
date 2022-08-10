package mx.com.lestradam.covid.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entities.Coordinate;

@Repository
public interface CoordinateRepository extends PagingAndSortingRepository<Coordinate, Long> {
	
	public Page<Coordinate> findByIdGreaterThan(long id, Pageable pageable);

}
