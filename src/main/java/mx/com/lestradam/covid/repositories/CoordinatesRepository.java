package mx.com.lestradam.covid.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import mx.com.lestradam.covid.entites.Coordinates;

@Repository
public interface CoordinatesRepository extends PagingAndSortingRepository<Coordinates, Long> {

	public List<Coordinates> findByIdGreaterThan(long id);
}
