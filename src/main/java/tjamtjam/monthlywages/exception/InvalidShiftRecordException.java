package tjamtjam.monthlywages.exception;

/**
 * This exception signals that there is an invalid record in the work shift data.
 * @author tjamtjam
 *
 */
public class InvalidShiftRecordException extends Exception {

	public static enum ExceptionType {
		MISSING_PERSON_ID, MISSING_PERSON_NAME, INVALID_DATE, INVALID_START_TIME, INVALID_END_TIME,
		WRONG_NUMBER_OF_DATA_ELEMENTS;
	}

	/**
	 * Which record is the first invalid one.
	 */
	private long recordNumber;
	/**
	 * The type of invalidity.
	 */
	private ExceptionType type;
	/**
	 * The data that resulted in this exception.
	 */
	private String data;

	public InvalidShiftRecordException(long recordNumber, ExceptionType type) {
		this(recordNumber, type, null, null);
	}
	
	public InvalidShiftRecordException(long recordNumber, ExceptionType type, String data, Throwable cause) {
		super(buildMessage(recordNumber, data, type), cause);
		this.recordNumber = recordNumber;
		this.type = type;
		this.data = data;
	}

	private static String buildMessage(long recordNumber, String data, ExceptionType type) {
		StringBuilder sb = new StringBuilder("Record ").append(recordNumber).append(" is invalid! ");
		switch (type) {
		case MISSING_PERSON_ID:
			sb.append("Person ID is missing.");
			break;
		case MISSING_PERSON_NAME:
			sb.append("Person name is missing.");
			break;
		case INVALID_DATE:
			sb.append("Date ").append("'").append(data).append("is invalid.");
			break;
		case INVALID_START_TIME:
			sb.append("Start time ").append("'").append(data).append("is invalid.");
			break;
		case INVALID_END_TIME:
			sb.append("End time ").append("'").append(data).append("is invalid.");
			break;
		case WRONG_NUMBER_OF_DATA_ELEMENTS:
			sb.append("Wrong number of data elements! (").append(data).append(")");
			break;
		}
		return sb.toString();
	}

	public long getRecordNumber() {
		return recordNumber;
	}

	public ExceptionType getType() {
		return type;
	}

	public String getData() {
		return data;
	}

}
