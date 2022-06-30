package mx.com.lestradam.covid.config;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mx.com.lestradam.covid.batch.CoordinatesItemProcessor;
import mx.com.lestradam.covid.batch.CustomItemReaders;
import mx.com.lestradam.covid.batch.CustomItemWriters;
import mx.com.lestradam.covid.batch.RetrieveInfoTasklet;
import mx.com.lestradam.covid.dto.CoordinatesDTO;
import mx.com.lestradam.covid.entites.Site;

@Configuration
@EnableBatchProcessing
@Import({CustomItemReaders.class, CustomItemWriters.class})
public class BatchConfiguration {
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public ItemReader<Site> sitesItemReader;
	
	@Autowired
	public ItemWriter<CoordinatesDTO> coordinatesItemWriter;
	
	@Bean 
	public Tasklet retrieveInfoTasklet() {
		return new RetrieveInfoTasklet();
	}
	@Bean
	public ItemProcessor<Site, CoordinatesDTO> itemProcessor(){
		return new CoordinatesItemProcessor();
	}
	
	@Bean
	public Step retrieveInfoStep() {
		return this.stepBuilderFactory.get("retrieveInfoStep").tasklet(retrieveInfoTasklet()).build();
	}	
	
	@Bean
	public Step chunkBasedStep() {
		return this.stepBuilderFactory.get("coodinatesStep")
				.<Site, CoordinatesDTO>chunk(10)
				.reader(sitesItemReader)
				.processor(itemProcessor())
				.writer(coordinatesItemWriter)
				.build();
	}
	
	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory) {
		return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.start(retrieveInfoStep())
				.next(chunkBasedStep())
				.build();
	}

}
