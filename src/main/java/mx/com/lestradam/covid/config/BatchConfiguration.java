package mx.com.lestradam.covid.config;

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

import mx.com.lestradam.covid.batch.CoordinatesItemProcessor;
import mx.com.lestradam.covid.batch.RetrieveInfoTasklet;
import mx.com.lestradam.covid.constants.BatchConstants;
import mx.com.lestradam.covid.entites.Coordinates;
import mx.com.lestradam.covid.entites.TravelCost;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public ItemProcessor<Coordinates, Coordinates> coordinateItemProcessor(){
		return new CoordinatesItemProcessor();
	}
	
	@Bean 
	public Tasklet retrieveInfoTasklet() {
		return new RetrieveInfoTasklet();
	}
	
	@Bean
	public Step travelCostStep(ItemReader<TravelCost> reader, ItemWriter<TravelCost> writer) {
		return this.stepBuilderFactory.get("distanceMatravelCostSteptrixStep")
				.<TravelCost, TravelCost>chunk(BatchConstants.CHUNK_SIZE_TRAVEL_COST)
				.reader(reader)
				.writer(writer)
				.build();
	}
	
	@Bean
	public Step distanceMatrixStep(ItemReader<Coordinates> reader, ItemWriter<Coordinates> writer) {
		return this.stepBuilderFactory.get("distanceMatrixStep")
				.<Coordinates, Coordinates>chunk(BatchConstants.CHUNK_SIZE_COORDINATES)
				.reader(reader)
				.processor(coordinateItemProcessor())
				.writer(writer)
				.build();
	}

	@Bean
	public Step retrieveInfoStep() {
		return this.stepBuilderFactory.get("retrieveInfoStep").tasklet(retrieveInfoTasklet()).build();
	}	
	
	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, Step distanceMatrixStep, Step travelCostStep) {
		return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.start(retrieveInfoStep())
				.next(distanceMatrixStep)
				.next(travelCostStep)
				.build();
	}

}
