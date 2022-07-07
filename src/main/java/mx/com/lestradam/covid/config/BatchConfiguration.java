package mx.com.lestradam.covid.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mx.com.lestradam.covid.batch.RetrieveInfoTasklet;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Bean 
	public Tasklet retrieveInfoTasklet() {
		return new RetrieveInfoTasklet();
	}

	@Bean
	public Step retrieveInfoStep() {
		return this.stepBuilderFactory.get("retrieveInfoStep").tasklet(retrieveInfoTasklet()).build();
	}	
	
	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory) {
		return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.start(retrieveInfoStep())
				.build();
	}

}
