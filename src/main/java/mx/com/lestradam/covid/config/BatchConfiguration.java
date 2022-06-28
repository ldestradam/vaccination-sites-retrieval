package mx.com.lestradam.covid.config;

import java.io.IOException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import mx.com.lestradam.covid.batch.CoordinatesItemProcessor;
import mx.com.lestradam.covid.dto.CoordinatesDTO;
import mx.com.lestradam.covid.dto.SedeDTO;
import mx.com.lestradam.covid.mappers.SedeFieldSetMapper;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Value("${input.file.xlsx}")
	public String xlsxFile;
	
	@Value("${output.file.json}")
	public String jsonFile;
	
	public static String[] tokens = new String[] {"No. Sede", "Municipio","Sede","Coordenadas"};
	
	@Bean
	public ItemReader<SedeDTO> itemReader() throws IOException{
		FlatFileItemReader<SedeDTO> itemReader = new FlatFileItemReader<>();
		itemReader.setLinesToSkip(1);
		itemReader.setResource( new FileSystemResource(xlsxFile));
		DefaultLineMapper<SedeDTO> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(tokens);
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(new SedeFieldSetMapper());
		itemReader.setLineMapper(lineMapper);
		return itemReader;
	}
	
	@Bean
	public ItemWriter<CoordinatesDTO> itemWriter() throws IOException {
		return new JsonFileItemWriterBuilder<CoordinatesDTO>()
				.jsonObjectMarshaller(new JacksonJsonObjectMarshaller<CoordinatesDTO>())
				.resource(new FileSystemResource(jsonFile))
				.name("coordenatesJsonFileItemWriter")
				.build();
	}
	
	@Bean
	public ItemProcessor<SedeDTO, CoordinatesDTO> itemProcessor(){
		return new CoordinatesItemProcessor();
	}
	
	@Bean
	public Step chunkBasedStep() throws IOException {
		return this.stepBuilderFactory.get("coodinatesStep")
				.<SedeDTO, CoordinatesDTO>chunk(10)
				.reader(itemReader())
				.processor(itemProcessor())
				.writer(itemWriter())
				.build();
	}
	
	@Bean
	public Job job() throws IOException {
		return this.jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.start(chunkBasedStep())
				.build();
	}

}
