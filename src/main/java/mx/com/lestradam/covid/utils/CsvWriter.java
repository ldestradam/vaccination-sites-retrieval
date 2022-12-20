package mx.com.lestradam.covid.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVWriter;

import mx.com.lestradam.covid.exceptions.DataException;

public class CsvWriter {
	private CsvWriter() {}
	
	public static void createFile(final String filePath, final List<String[]> rows) {
		try (CSVWriter file = new CSVWriter(new FileWriter(filePath, true))){
			for (String[] row : rows) {
				file.writeNext(row);
			}
		} catch (IOException e) {
			throw new DataException("Error writing node file", e);
		}
	}

}
