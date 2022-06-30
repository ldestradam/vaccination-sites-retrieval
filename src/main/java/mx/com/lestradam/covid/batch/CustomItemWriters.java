package mx.com.lestradam.covid.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import mx.com.lestradam.covid.dto.CoordinatesDTO;

@Configuration
public class CustomItemWriters {
	
	@Value("${output.file.json}")
	private String jsonFile;
	
	@Bean
	public ItemWriter<CoordinatesDTO> jsonCoordinatesItemWriter() {
		return new JsonFileItemWriterBuilder<CoordinatesDTO>()
				.jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
				.resource(new FileSystemResource(jsonFile))
				.name("coordenatesJsonFileItemWriter")
				.build();
	}

}
