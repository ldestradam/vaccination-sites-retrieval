package mx.com.lestradam.covid.batch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mx.com.lestradam.covid.constants.BatchConstants;
import mx.com.lestradam.covid.entities.Coordinate;
import mx.com.lestradam.covid.entities.Dose;

@Configuration
public class CustomItemReaders {
	
	@Bean
	public ItemReader<Coordinate> coordinatesItemReader(EntityManagerFactory entityManagerFactory) {
		return new JpaPagingItemReaderBuilder<Coordinate>()
			.name("coordinatesItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("select s from Coordinate s")
			.pageSize(BatchConstants.CHUNK_SIZE_COORDINATES)
			.saveState(false)
			.build();
	}
	
	@Bean
	public ItemReader<Dose> dosesItemReader(EntityManagerFactory entityManagerFactory) {
		return new JpaPagingItemReaderBuilder<Dose>()
			.name("dosesItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("select s from Dose s")
			.pageSize(BatchConstants.CHUNK_SIZE_DOSES)
			.saveState(false)
			.build();
	}

}
