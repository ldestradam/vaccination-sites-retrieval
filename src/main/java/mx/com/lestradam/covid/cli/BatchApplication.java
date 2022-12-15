package mx.com.lestradam.covid.cli;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.covid.exceptions.GeneralBatchExecption;

@Component
public class BatchApplication {
	
	@Autowired
    private JobLauncher jobLauncher;
	
    @Autowired
    private Job job;
	
	public void execute(String inputFile) {
		JobParameters parameters = new JobParametersBuilder()
				.addString("input.file", inputFile)
				.toJobParameters();
		try {
			jobLauncher.run(job, parameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			throw new GeneralBatchExecption("Error in execution of the batch process", e);
		}
	}

}
