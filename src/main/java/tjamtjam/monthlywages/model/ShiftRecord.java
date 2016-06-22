package tjamtjam.monthlywages.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.concurrent.TimeUnit;

import tjamtjam.monthlywages.MathUtils;

/**
 * This class contains data from one shift: the person, date, start and end time.
 * @author tjamtjam
 *
 */
public class ShiftRecord {

	private static final BigDecimal MILLIS_IN_HOUR = BigDecimal.valueOf(TimeUnit.HOURS.toMillis(1));
	
	private Person person;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;

	public ShiftRecord(Person person, LocalDate date, LocalTime startTime, LocalTime endTime) {
		this.person = person;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Person getPerson() {
		return person;
	}

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}
	
	private long getStartMillis() {
		return this.startTime.getLong(ChronoField.MILLI_OF_DAY);
	}
	
	private long getEndMillis() {
		long endMillis = this.endTime.getLong(ChronoField.MILLI_OF_DAY);
		if (endMillis < this.getStartMillis()) {
			endMillis += TimeUnit.DAYS.toMillis(1);
		}
		return endMillis;
	}

	public BigDecimal getTotalHours() {
		long startMillis = this.getStartMillis();
		long endMillis = this.getEndMillis();
		BigDecimal totalMillis = BigDecimal.valueOf(endMillis - startMillis);
		return totalMillis.divide(MILLIS_IN_HOUR, MathUtils.MC);
	}
	
	public BigDecimal getEveningHours(LocalTime eveningStart, LocalTime eveningEnd) {
		if (eveningStart.isBefore(eveningEnd)) {
			throw new IllegalArgumentException("Evening start time should be after the evening end time");
		}
		
		long startMillis = this.getStartMillis();
		long endMillis = this.getEndMillis();
		long eveningStartMillis = eveningStart.getLong(ChronoField.MILLI_OF_DAY);
		long eveningEndMillis = eveningEnd.getLong(ChronoField.MILLI_OF_DAY) + TimeUnit.DAYS.toMillis(1);
		
		if (startMillis < eveningEndMillis - TimeUnit.DAYS.toMillis(1)) {
			/* Shift starts before the evening ends. Shift the start and end time a day forward so the range created
			 * by them overlaps the evening range.
			 */
			startMillis += TimeUnit.DAYS.toMillis(1);
			endMillis += TimeUnit.DAYS.toMillis(1);
		}
		
		if (endMillis > eveningStartMillis) {
			/* Hours in the evening start from the shift beginning if the shift started after the evening starter
			 * or at the start of the evening if the shift started before the evening.
			 * Hours in the evening end at the end of the shift if the shift ends before the evening or at the end
			 * of the evening if the shift ends after the evening.
			 */
			long eveningMillis = Math.min(endMillis, eveningEndMillis) - Math.max(startMillis, eveningStartMillis);
			return BigDecimal.valueOf(eveningMillis).divide(MILLIS_IN_HOUR, MathUtils.MC);
		}
		else {
			// Shift ended before the evening
			return BigDecimal.ZERO;
		}
	}
}
