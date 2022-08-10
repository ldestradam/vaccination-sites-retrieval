package mx.com.lestradam.covid.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import mx.com.lestradam.covid.batch.CoordinatesItemProcessor;
import mx.com.lestradam.covid.batch.DistanceMatrixStepListener;
import mx.com.lestradam.covid.batch.RetrieveInfoTasklet;
import mx.com.lestradam.covid.constants.BatchConstants;
import mx.com.lestradam.covid.entities.Coordinate;
import mx.com.lestradam.covid.entities.Cost;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public StepExecutionListener distanceMatrixStepListener() {
		return new DistanceMatrixStepListener();
	}
	
	@Bean
	public ItemProcessor<Coordinate, Coordinate> coordinateItemProcessor(){
		return new CoordinatesItemProcessor();
	}
	
	@Bean 
	public Tasklet retrieveInfoTasklet() {
		return new RetrieveInfoTasklet();
	}
	
	@Bean
	public Step travelCostCsvStep(ItemReader<Cost> travelCostReader, @Qualifier("travelCostCsvItemWriter") ItemWriter<Cost> travelCostCsvWriter) {
		return this.stepBuilderFactory.get("travelCostCsvStep")
				.<Cost, Cost>chunk(BatchConstants.CHUNK_SIZE_TRAVEL_COST)
				.reader(travelCostReader)
				.writer(travelCostCsvWriter)
				.build();
	}
	
	@Bean
	public Step travelCostJsonStep(ItemReader<Cost> travelCostReader, @Qualifier("travelCostJsonItemWriter") ItemWriter<Cost> travelCostJsonWriter) {
		return this.stepBuilderFactory.get("travelCostJsonStep")
				.<Cost, Cost>chunk(BatchConstants.CHUNK_SIZE_TRAVEL_COST)
				.reader(travelCostReader)
				.writer(travelCostJsonWriter)
				.build();
	}
	
	@Bean
	public Step travelCostXmlStep(ItemReader<Cost> travelCostReader, @Qualifier("travelCostXmlItemWriter") ItemWriter<Cost> travelCostXmlItemWriter) {
		return this.stepBuilderFactory.get("travelCostXmlStep")
				.<Cost, Cost>chunk(BatchConstants.CHUNK_SIZE_TRAVEL_COST)
				.reader(travelCostReader)
				.writer(travelCostXmlItemWriter)
				.build();
	}
	
	@Bean
	public Step distanceMatrixStep(ItemReader<Coordinate> reader, ItemWriter<Coordinate> writer) {
		return this.stepBuilderFactory.get("distanceMatrixStep")
				.<Coordinate, Coordinate>chunk(BatchConstants.CHUNK_SIZE_COORDINATES)
				.reader(reader)
				.processor(coordinateItemProcessor())
				.writer(writer)
				.listener(distanceMatrixStepListener())
				.build();
	}

	@Bean
	public Step retrieveInfoStep() {
		return this.stepBuilderFactory.get("retrieveInfoStep").tasklet(retrieveInfoTasklet()).build();
	}
	
	@Bean
	public Flow travelCostCsvFlow(Step travelCostCsvStep) {
		return new FlowBuilder<SimpleFlow>("travelCostCsvFlow").start(travelCostCsvStep).build();
	}
	
	@Bean
	public Flow travelCostXmlFlow(Step travelCostXmlStep) {
		return new FlowBuilder<SimpleFlow>("travelCostXmlFlow").start(travelCostXmlStep).build();
	}
	
	@Bean
	public Flow travelCostJsonFlow(Step travelCostJsonStep) {
		return new FlowBuilder<SimpleFlow>("travelCostJsonFlow").start(travelCostJsonStep).build();
	}
	
	@Bean
	public Flow filesGenerationFlow(Flow travelCostJsonFlow, Flow travelCostCsvFlow, Flow travelCostXmlFlow) {
		return new FlowBuilder<SimpleFlow>("filesGenerationFlow")
			.split(new SimpleAsyncTaskExecutor())
			.add(travelCostCsvFlow, travelCostJsonFlow, travelCostXmlFlow)
			.build();
	}

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, Step distanceMatrixStep, Flow filesGenerationFlow) {
		return jobBuilderFactory.get("job")
			.incrementer(new RunIdIncrementer())
			.start(retrieveInfoStep())
			.next(distanceMatrixStep)
			.on("COMPLETED").to(filesGenerationFlow).end()
			.build();
	}

}
