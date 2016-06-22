package tjamtjam.monthlywages.service;

import java.io.IOException;
import java.io.InputStream;

import tjamtjam.monthlywages.exception.InvalidShiftListException;
import tjamtjam.monthlywages.exception.InvalidShiftRecordException;
import tjamtjam.monthlywages.model.MonthlyWages;
import tjamtjam.monthlywages.model.WageSettings;

public interface WageService {

	/**
	 * <p>Computes the monthly wages from the given {@link InputStream}. The input stream should contain data as comma separated
	 * values. Each record in the data should be in format <code>Person Name, Person ID, Date, Start, End</code>. Records
	 * should be separated by line breaks and the data should be encoded with UTF-8. The first record is considered an header
	 * and ignored.</p>
	 * <p><h3>Data elements in one data record</h3>
	 * 	<ul>
	 * 		<li><strong>Person name</strong>: Textual name</li>
	 * 		<li><strong>Person ID</strong>: Unique ID</li>
	 * 		<li><strong>Date</strong>: work shift date in format <code>d.M.yyyy</code></li>
	 * 		<li><strong>Start</strong>: shift start time in format <code>H:mm</code></li>
	 * 		<li><strong>End</strong>: shift end time in format <code>H:mm</code></li>
	 * 	</ul>
	 * </p>
	 * @param in
	 * @param settings The {@link WageSettings} used during the computation.
	 * @return The wage information.
	 * @throws IOException If reading the input stream fails.
	 * @throws InvalidShiftRecordException If the data contains an invalid shift record.
	 * @throws InvalidShiftListException If the data is invalid. E.g. if it does not contain any records.
	 */
	MonthlyWages computeMonthlyWages(InputStream in, WageSettings settings) throws IOException, InvalidShiftRecordException, InvalidShiftListException;
	
}
