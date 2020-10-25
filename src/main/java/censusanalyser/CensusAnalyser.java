package censusanalyser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import com.bl.creatingJar.CSVBuilderException;
import com.bl.creatingJar.CSVBuilderFactory;
import com.bl.creatingJar.ICSVBuilder;
import com.google.gson.Gson;

public class CensusAnalyser {

	List<IndiaCensusCSV> censusCSVList = null;

	// validation check of the file using regex
	public boolean checkValidityOfFile(String csvFilePath) {
		String filePattern = "^([a-z A-Z 0-9]+).csv$";
		return Pattern.matches(filePattern, csvFilePath);
	}

	// checking if the given file is valid or not
	public void isFileValid(String csvFilePath) throws CensusAnalyserException {
		Path path = Paths.get(csvFilePath);
		if (checkValidityOfFile(path.getFileName().toString()) == false) {
			throw new CensusAnalyserException("Enter proper file type",
					CensusAnalyserException.ExceptionType.WRONG_FILE_TYPE);
		}
	}

	// loading indian census data file for analysis
	public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
		isFileValid(csvFilePath);
		try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
			ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
			censusCSVList = csvBuilder.getCSVFileList(reader, IndiaCensusCSV.class);
			return censusCSVList.size();
		} catch (IOException e) {
			throw new CensusAnalyserException(e.getMessage(),
					CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
		} catch (RuntimeException e) {
			throw new CensusAnalyserException(e.getMessage(), CensusAnalyserException.ExceptionType.NOT_PROPER_CSV);
		} catch (CSVBuilderException e) {
			throw new CensusAnalyserException(e.getMessage(), e.type.name());
		}
	}

	// loading indian state code data file for analysis
	public int loadIndianStateCode(String csvFilePath) throws CensusAnalyserException {
		isFileValid(csvFilePath);
		try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
			ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
			Iterator<IndiaStateCodeCSV> stateCSVIterator = csvBuilder.getCSVFileIterator(reader,
					IndiaStateCodeCSV.class);
			return this.getCount(stateCSVIterator);
		} catch (IOException e) {
			throw new CensusAnalyserException(e.getMessage(),
					CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
		} catch (RuntimeException e) {
			throw new CensusAnalyserException(e.getMessage(), CensusAnalyserException.ExceptionType.NOT_PROPER_CSV);
		} catch (CSVBuilderException e) {
			throw new CensusAnalyserException(e.getMessage(), e.type.name());
		}
	}

	// getting count of the entries in the file
	private <E> int getCount(Iterator<E> iterator) {
		Iterable<E> csvIterable = () -> iterator;
		int numOfEntries = (int) StreamSupport.stream(csvIterable.spliterator(), false).count();
		return numOfEntries;
	}

	// sorting the list according to states name
	public String getStateWiseSortedCensusData() throws CensusAnalyserException {
		if (censusCSVList == null || censusCSVList.size() == 0) {
			throw new CensusAnalyserException("No csv data", CensusAnalyserException.ExceptionType.NO_CSV_DATA);
		}
		Comparator<IndiaCensusCSV> censusComparator = Comparator.comparing(census -> census.state);
		this.sort(censusComparator);
		String sortedStateCensusJson = new Gson().toJson(censusCSVList);
		return sortedStateCensusJson;
	}

	// sorting the list by bubble sort technique
	private void sort(Comparator<IndiaCensusCSV> censusComparator) {
		for (int i = 0; i < censusCSVList.size() - 1; i++) {
			for (int j = 0; j < censusCSVList.size() - i - 1; j++) {
				IndiaCensusCSV census1 = censusCSVList.get(j);
				IndiaCensusCSV census2 = censusCSVList.get(j + 1);
				if (censusComparator.compare(census1, census2) > 0) {
					censusCSVList.set(j, census2);
					censusCSVList.set(j + 1, census1);
				}
			}
		}
	}
}
