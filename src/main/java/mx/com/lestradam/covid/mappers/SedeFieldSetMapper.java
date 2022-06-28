package mx.com.lestradam.covid.mappers;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import mx.com.lestradam.covid.dto.SedeDTO;

public class SedeFieldSetMapper  implements FieldSetMapper<SedeDTO>{

	@Override
	public SedeDTO mapFieldSet(FieldSet fieldSet) throws BindException {
		SedeDTO sede = new SedeDTO();
		sede.setDescription(fieldSet.readString("Sede"));
		sede.setId(fieldSet.readLong("No. Sede"));
		sede.setCoordinates(fieldSet.readString("Coordenadas"));
		return sede;
	}


}
