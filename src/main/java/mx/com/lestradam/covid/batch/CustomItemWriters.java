package mx.com.lestradam.covid.batch;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import mx.com.lestradam.covid.entites.Coordinates;
import mx.com.lestradam.covid.entites.TravelCost;

@Configuration
public class CustomItemWriters {
	
	@Value("${output.file.json}")
	private String jsonFilePath;
	
	@Value("${output.file.csv}")
	private String csvFilePath;
	
	@Value("${output.file.xml}")
	private String xmlFilePath;
	
	private static final String[] names = new String[] { "id", "idSiteFrom", "idSiteTo", "distance", "duration", "status"};
	
	@Bean(name = "travelCostCsvItemWriter")
	public ItemWriter<TravelCost> travelCostCsvItemWriter() {
		FlatFileItemWriter<TravelCost> itemWriter = new FlatFileItemWriter<>();		
		itemWriter.setResource(new FileSystemResource(csvFilePath));		
		DelimitedLineAggregator<TravelCost> aggregator = new DelimitedLineAggregator<>();
		aggregator.setDelimiter(",");		
		BeanWrapperFieldExtractor<TravelCost> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(names);
		aggregator.setFieldExtractor(fieldExtractor);
		itemWriter.setLineAggregator(aggregator);
		return itemWriter;
	}
	
	@Bean(name = "travelCostJsonItemWriter")
	public ItemWriter<TravelCost> travelCostJsonItemWriter() {
		return new JsonFileItemWriterBuilder<TravelCost>()
				.jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
				.resource(new FileSystemResource(jsonFilePath))
				.name("travelCostJsonItemWriter")
				.build();
	}
	
	@Bean(name = "travelCostXmlItemWriter")
	public ItemWriter<TravelCost> travelCostXmlItemWriter() {	
		Map<String, Class<TravelCost> > aliases = new HashMap<>();
		aliases.put("travelcost", TravelCost.class);
		XStreamMarshaller marshaller = new XStreamMarshaller();
		marshaller.setAliases(aliases);
		
		return new StaxEventItemWriterBuilder<TravelCost>()
			.name("studentWriter")
			.resource(new FileSystemResource(xmlFilePath))
			.marshaller(marshaller)
			.rootTagName("travelcosts")
			.build();
	}
	
	@Bean
	public ItemWriter<Coordinates> coordinatesJpaItemWriter(EntityManagerFactory entityManagerFactory) {
		  JpaItemWriter<Coordinates> iwriter = new JpaItemWriter<>();
		  iwriter.setEntityManagerFactory(entityManagerFactory);
		  return iwriter;
	}

}
