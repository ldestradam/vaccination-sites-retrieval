package mx.com.lestradam.covid.batch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import mx.com.lestradam.covid.entites.Coordinates;
import mx.com.lestradam.covid.entites.TravelCost;

@Configuration
public class CustomItemWriters {
	
	@Value("${output.file.json}")
	private String jsonFile;
	
	
	@Bean
	public ItemWriter<TravelCost> travelCostJsonItemWriter() {
		return new JsonFileItemWriterBuilder<TravelCost>()
				.jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
				.resource(new FileSystemResource(jsonFile))
				.name("travelCostJsonItemWriter")
				.build();
	}
	
	@Bean
	public ItemWriter<Coordinates> coordinatesJpaItemWriter(EntityManagerFactory entityManagerFactory) {
		  JpaItemWriter<Coordinates> iwriter = new JpaItemWriter<>();
		  iwriter.setEntityManagerFactory(entityManagerFactory);
		  return iwriter;
	}

}
