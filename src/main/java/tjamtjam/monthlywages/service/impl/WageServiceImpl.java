package tjamtjam.monthlywages.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import tjamtjam.monthlywages.exception.InvalidShiftListException;
import tjamtjam.monthlywages.exception.InvalidShiftRecordException;
import tjamtjam.monthlywages.exception.InvalidShiftRecordException.ExceptionType;
import tjamtjam.monthlywages.model.MonthlyWage;
import tjamtjam.monthlywages.model.MonthlyWages;
import tjamtjam.monthlywages.model.Person;
import tjamtjam.monthlywages.model.ShiftRecord;
import tjamtjam.monthlywages.model.WageSettings;
import tjamtjam.monthlywages.service.WageService;

@Service
public class WageServiceImpl implements WageService {

	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy");
	private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
	
	@Override
	public MonthlyWages computeMonthlyWages(InputStream in, WageSettings settings) throws IOException, InvalidShiftRecordException, InvalidShiftListException {
		Map<String, Person> personIdToPerson = new HashMap<>();
		Map<Person, MonthlyWage> personToMonthlyWage = new HashMap<>();
		LocalDate minDate = null;
		
		CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
		CSVParser parser = null;
		try {
			parser = new CSVParser(new InputStreamReader(in, Charset.forName("UTF-8")), format);
			for (CSVRecord record : parser) {
				ShiftRecord wageRecord = this.csvRecordToWageRecord(record, personIdToPerson);
				MonthlyWage monthlyWage = personToMonthlyWage.get(wageRecord.getPerson());
				if (monthlyWage == null) {
					monthlyWage = new MonthlyWage(wageRecord.getPerson(), settings);
					personToMonthlyWage.put(monthlyWage.getPerson(), monthlyWage);
				}
				monthlyWage.addWageRecord(wageRecord);
				if (minDate == null || minDate.isAfter(wageRecord.getDate())) {
					minDate = wageRecord.getDate();
				}
			}
		}
		finally {
			if (parser != null) {
				parser.close();
			}
		}
		
		if (personToMonthlyWage.isEmpty()) {
			throw new InvalidShiftListException();
		}
		return new MonthlyWages(minDate.withDayOfMonth(1), personToMonthlyWage.values());
	}

	/**
	 * Converts a {@link CSVRecord} to a {@link ShiftRecord} that can be analyzed.
	 * @param csvRecord
	 * @param personIdToPerson
	 * @return
	 * @throws InvalidShiftRecordException If parsing a record fails.
	 */
	private ShiftRecord csvRecordToWageRecord(CSVRecord csvRecord, Map<String, Person> personIdToPerson) throws InvalidShiftRecordException {
		if (csvRecord.size() != 5) {
			throw new InvalidShiftRecordException(csvRecord.getRecordNumber(), ExceptionType.WRONG_NUMBER_OF_DATA_ELEMENTS, Integer.toString(csvRecord.size()), null);
		}
		
		String personId = csvRecord.get(1);
		if (StringUtils.isBlank(personId)) {
			throw new InvalidShiftRecordException(csvRecord.getRecordNumber(), ExceptionType.MISSING_PERSON_ID);
		}
		else {
			personId = personId.trim();
		}
		Person person = personIdToPerson.get(personId);
		if (person == null) {
			String personName = csvRecord.get(0);
			if (StringUtils.isBlank(personName)) {
				throw new InvalidShiftRecordException(csvRecord.getRecordNumber(), ExceptionType.MISSING_PERSON_NAME);
			}
			person = new Person(personId, personName.trim());
			personIdToPerson.put(personId, person);
		}
		
		String dateString = csvRecord.get(2);
		if (StringUtils.isBlank(dateString)) {
			throw new InvalidShiftRecordException(csvRecord.getRecordNumber(), ExceptionType.INVALID_DATE, dateString, null);
		}
		LocalDate date;
		try {
			date = LocalDate.parse(dateString.trim(), dateFormat);
		}
		catch (DateTimeParseException e) {
			throw new InvalidShiftRecordException(csvRecord.getRecordNumber(), ExceptionType.INVALID_DATE, dateString, e);
		}
		
		LocalTime startTime = parseTime(csvRecord.getRecordNumber(), csvRecord.get(3), ExceptionType.INVALID_START_TIME);
		LocalTime endTime = parseTime(csvRecord.getRecordNumber(), csvRecord.get(4), ExceptionType.INVALID_END_TIME);
		
		return new ShiftRecord(person, date, startTime, endTime);
	}
	
	/**
	 * Parses a {@link LocalTime} from the given <code>string</code>.
	 * @param recordNumber
	 * @param string
	 * @param exceptionType If the parsing fails, this is given to the thrown {@link InvalidShiftRecordException}.
	 * @return
	 * @throws InvalidShiftRecordException If no {@link LocalTime} can be parsed from the given <code>string</code>.
	 */
	private static LocalTime parseTime(long recordNumber, String string, ExceptionType exceptionType) throws InvalidShiftRecordException {
		if (StringUtils.isBlank(string)) {
			throw new InvalidShiftRecordException(recordNumber, exceptionType, string, null);
		}
		try {
			return LocalTime.parse(string.trim(), timeFormat);
		}
		catch (DateTimeParseException e) {
			throw new InvalidShiftRecordException(recordNumber, exceptionType, string, e);
		}
	}
}
