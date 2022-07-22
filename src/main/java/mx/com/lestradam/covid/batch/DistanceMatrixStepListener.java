package mx.com.lestradam.covid.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.lestradam.covid.constants.BatchConstants;
import mx.com.lestradam.covid.repositories.CoordinatesRepository;

public class DistanceMatrixStepListener implements StepExecutionListener{
	
	private Logger logger = LoggerFactory.getLogger(DistanceMatrixStepListener.class);
	
	@Autowired
	private CoordinatesRepository coordRepository;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.info("Total sites to process: {}", coordRepository.countByStatus(BatchConstants.INITIAL_STATUS));
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		logger.info("Successful sites processed: {}", coordRepository.countByStatus(BatchConstants.SUCCESS_STATUS));
		logger.info("Processed failed sites: {}", coordRepository.countByStatus(BatchConstants.FAILED_STATUS));
		return stepExecution.getExitStatus();
	}

}
