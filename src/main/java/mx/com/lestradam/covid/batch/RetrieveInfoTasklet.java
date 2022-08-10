package mx.com.lestradam.covid.batch;

import java.util.Optional;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.lestradam.covid.components.XlsxReader;
import mx.com.lestradam.covid.exceptions.FileReaderException;

public class RetrieveInfoTasklet implements Tasklet{
	
	@Autowired
	private XlsxReader xlsxReader;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {		
		Optional<Object> optFilePath = Optional.ofNullable(chunkContext.getStepContext().getJobParameters().get("input.file"));
		if(!optFilePath.isPresent())
			throw new FileReaderException("File path not provided");		
		xlsxReader.retrieveDataFromXlsx(optFilePath.get().toString());
		return RepeatStatus.FINISHED;
	}

}
