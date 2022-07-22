package mx.com.lestradam.covid.batch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import mx.com.lestradam.covid.constants.BatchConstants;
import mx.com.lestradam.covid.entites.Coordinates;
import mx.com.lestradam.covid.entites.TravelCost;

@Configuration
public class CustomItemReaders {
	
	@Bean
	public ItemReader<Coordinates> coordinatesItemReader(EntityManagerFactory entityManagerFactory) {
		return new JpaPagingItemReaderBuilder<Coordinates>()
			.name("coordinatesItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("select s from Coordinates s")
			.pageSize(BatchConstants.CHUNK_SIZE_COORDINATES)
			.saveState(false)
			.build();
	}
	
	@Bean
	@Scope(value = "prototype")
	public ItemReader<TravelCost> travelCostItemReader(EntityManagerFactory entityManagerFactory) {
		return new JpaPagingItemReaderBuilder<TravelCost>()
			.name("travelCostItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("select tc from TravelCost tc")
			.pageSize(BatchConstants.CHUNK_SIZE_TRAVEL_COST)
			.saveState(false)
			.build();
	}

}
