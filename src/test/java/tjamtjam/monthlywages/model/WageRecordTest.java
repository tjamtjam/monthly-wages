package tjamtjam.monthlywages.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class WageRecordTest {

	private static Person person = new Person("19", "Jospel Kedettunen");
	private static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
	private static LocalTime eveningStart = LocalTime.parse("18:00", timeFormat);
	private static LocalTime eveningEnd = LocalTime.parse("6:00", timeFormat);
	
	@Test
	public void testGetTotalHours() {
		assertEquals(BigDecimal.valueOf(8), createWageRecord("9:00", "17:00").getTotalHours());
		assertEquals(new BigDecimal("10.25"), createWageRecord("9:30", "19:45").getTotalHours());
		assertEquals(new BigDecimal("7.75"), createWageRecord("19:45", "3:30").getTotalHours());
	}
	
	@Test
	public void testGetEveningHours() {
		// shift end < evening start
		assertEquals(BigDecimal.ZERO, createWageRecord("9:00", "17:00").getEveningHours(eveningStart, eveningEnd));
		// shift end > evening start && shift start < evening start
		assertEquals(new BigDecimal("1.25"), createWageRecord("10:00", "19:15").getEveningHours(eveningStart, eveningEnd));
		// shift end > evening start && shift start > evening start
		assertEquals(new BigDecimal("3"), createWageRecord("22:15", "1:15").getEveningHours(eveningStart, eveningEnd));
		// shift end > evening end && shift start > evening start
		assertEquals(new BigDecimal("2.75"), createWageRecord("3:15", "7:00").getEveningHours(eveningStart, eveningEnd));
		// shift end > evening end && shift start < evening start
		assertEquals(new BigDecimal("12"), createWageRecord("17:00", "7:00").getEveningHours(eveningStart, eveningEnd));
	}
	
	private static ShiftRecord createWageRecord(String startTime, String endTime) {
		return new ShiftRecord(person, LocalDate.now(), LocalTime.parse(startTime, timeFormat), LocalTime.parse(endTime, timeFormat));
	}
}
