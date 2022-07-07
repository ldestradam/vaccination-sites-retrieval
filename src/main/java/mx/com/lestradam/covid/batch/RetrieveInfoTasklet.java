package mx.com.lestradam.covid.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.lestradam.covid.services.XlsxReaderService;

public class RetrieveInfoTasklet implements Tasklet{
	
	@Autowired
	private XlsxReaderService xlsxReader;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String inputFilePath = chunkContext.getStepContext().getJobParameters().get("input.file").toString();		
		xlsxReader.retrieveDatafromXlsx(inputFilePath);
		return RepeatStatus.FINISHED;
	}

}
