package mx.com.lestradam.covid.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import mx.com.lestradam.covid.batch.CoordinatesItemProcessor;
import mx.com.lestradam.covid.batch.DistanceMatrixStepListener;
import mx.com.lestradam.covid.batch.DosesItemProcessor;
import mx.com.lestradam.covid.batch.RetrieveInfoTasklet;
import mx.com.lestradam.covid.constants.BatchConstants;
import mx.com.lestradam.covid.entities.Coordinate;
import mx.com.lestradam.covid.entities.Dose;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(BatchConstants.THREADS);
		executor.setMaxPoolSize(BatchConstants.THREADS);
		executor.setThreadNamePrefix("CustomThreadPool-");
		return executor;
	}
	
	@Bean
	StepExecutionListener distanceMatrixStepListener() {
		return new DistanceMatrixStepListener();
	}
	
	@Bean
	ItemProcessor<Coordinate, Coordinate> coordinateItemProcessor(){
		return new CoordinatesItemProcessor();
	}
	
	@Bean
	ItemProcessor<Dose, Dose> doseItemProcessor(){
		return new DosesItemProcessor();
	}
	
	@Bean 
	Tasklet retrieveInfoTasklet() {
		return new RetrieveInfoTasklet();
	}
	
	@Bean
	Step distanceMatrixStep(ItemReader<Coordinate> reader, ItemWriter<Coordinate> writer) {
		return this.stepBuilderFactory.get("distanceMatrixStep")
			.<Coordinate, Coordinate>chunk(BatchConstants.CHUNK_SIZE_COORDINATES)
			.reader(reader)
			.processor(coordinateItemProcessor())
			.writer(writer)
			.listener(distanceMatrixStepListener())
			.taskExecutor(taskExecutor())
			.build();
	}
	
	@Bean
	Step doseQuantityStep(ItemReader<Dose> reader, ItemWriter<Dose> writer) {
		return this.stepBuilderFactory.get("doseStep")
			.<Dose, Dose>chunk(BatchConstants.CHUNK_SIZE_DOSES)
			.reader(reader)
			.processor(doseItemProcessor())
			.writer(writer)
			.taskExecutor(taskExecutor())
			.build();
	}

	@Bean
	Step retrieveInfoStep() {
		return this.stepBuilderFactory.get("retrieveInfoStep").tasklet(retrieveInfoTasklet()).build();
	}

	@Bean
	Job job(JobBuilderFactory jobBuilderFactory, Step distanceMatrixStep,  Step doseQuantityStep) {
		return jobBuilderFactory.get("job")
			.incrementer(new RunIdIncrementer())
			.start(retrieveInfoStep())
			.next(doseQuantityStep)
			.next(distanceMatrixStep)
			.build();
	}

}
