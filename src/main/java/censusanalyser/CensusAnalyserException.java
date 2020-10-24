package censusanalyser;

public class CensusAnalyserException extends Exception {

	enum ExceptionType {
		CENSUS_FILE_PROBLEM, UNABLE_TO_PARSE, NOT_PROPER_CSV, WRONG_FILE_TYPE
	}

	public ExceptionType type;
	private String name;

	public CensusAnalyserException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}

	public CensusAnalyserException(String message, ExceptionType type, Throwable cause) {
		super(message, cause);
		this.type = type;
	}

	public CensusAnalyserException(String message, String name) {
		super(message);
		this.name = name;
	}
}
