package mx.com.lestradam.covid.batch;


import javax.persistence.EntityManagerFactory;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mx.com.lestradam.covid.entities.Coordinate;
import mx.com.lestradam.covid.entities.Dose;

@Configuration
public class CustomItemWriters {
	
	@Bean
	public ItemWriter<Coordinate> coordinatesJpaItemWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<Coordinate> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}
	
	@Bean
	public ItemWriter<Dose> dosesJpaItemWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<Dose> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

}
