package mx.com.lestradam.covid.batch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import mx.com.lestradam.covid.dto.SedeDTO;
import mx.com.lestradam.covid.entites.Site;
import mx.com.lestradam.covid.mappers.SedeFieldSetMapper;

@Configuration
public class CustomItemReaders {
	
	@Bean
	public ItemReader<Site> sitesItemReader(EntityManagerFactory entityManagerFactory) {
		return new JpaPagingItemReaderBuilder<Site>()
			.name("siteItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("select s from Site s")
			.pageSize(50)
			.saveState(false)
			.build();
	}

}
