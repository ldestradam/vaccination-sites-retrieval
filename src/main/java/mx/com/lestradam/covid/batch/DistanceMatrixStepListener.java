package mx.com.lestradam.covid.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class DistanceMatrixStepListener implements StepExecutionListener{
	
	private Logger logger = LoggerFactory.getLogger(DistanceMatrixStepListener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.info("Beginning cost calculation process");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return stepExecution.getExitStatus();
	}

}
