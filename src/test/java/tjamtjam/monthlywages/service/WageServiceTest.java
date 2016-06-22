package tjamtjam.monthlywages.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import tjamtjam.monthlywages.exception.InvalidShiftListException;
import tjamtjam.monthlywages.exception.InvalidShiftRecordException;
import tjamtjam.monthlywages.exception.InvalidShiftRecordException.ExceptionType;
import tjamtjam.monthlywages.model.MonthlyWage;
import tjamtjam.monthlywages.model.MonthlyWages;
import tjamtjam.monthlywages.model.OvertimeClass;
import tjamtjam.monthlywages.model.Person;
import tjamtjam.monthlywages.model.WageSettings;
import tjamtjam.monthlywages.service.impl.WageServiceImpl;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class WageServiceTest {

	private WageService service;
	
	@Before
	public void setUp() {
		this.service = new WageServiceImpl();
	}
	
	@Test
	public void testOnePerson() throws Exception {
		MonthlyWages wages = this.service.computeMonthlyWages(this.getClass().getResourceAsStream("onePerson.csv"), WageSettings.DEFAULT);
		assertEquals(LocalDate.parse("2016-06-01"), wages.getMonth());
		assertEquals(1, wages.getWages().size());
		MonthlyWage wage = wages.getWages().iterator().next();
		
		Person person = wage.getPerson();
		assertEquals("19", person.getId());
		assertEquals("Jospel Kedettunen", person.getName());
		assertBDEquals(BigDecimal.valueOf(24), wage.getRegularHours());
		assertBDEquals(new BigDecimal("12.5"), wage.getEveningHours());
		assertBDEquals(new BigDecimal("2.5"), wage.getOvertimeHours(new OvertimeClass(0, 2, new BigDecimal("1.25"))));
		assertBDEquals(new BigDecimal("2"), wage.getOvertimeHours(new OvertimeClass(2, 4, new BigDecimal("1.5"))));
		assertBDEquals(new BigDecimal("0"), wage.getOvertimeHours(new OvertimeClass(4, null, new BigDecimal("2"))));
		assertBDEquals(new BigDecimal("127.34"), wage.getWage().setScale(2, RoundingMode.HALF_UP));
	}
	
	@Test
	public void testOneRecord() throws Exception {
		MonthlyWages wages = this.service.computeMonthlyWages(this.getClass().getResourceAsStream("oneRecord.csv"), WageSettings.DEFAULT);
		assertEquals(LocalDate.parse("2016-06-01"), wages.getMonth());
		assertEquals(1, wages.getWages().size());
		MonthlyWage wage = wages.getWages().iterator().next();
		
		Person person = wage.getPerson();
		assertEquals("19", person.getId());
		assertEquals("Jospel Kedettunen", person.getName());
		assertBDEquals(BigDecimal.valueOf(8), wage.getRegularHours());
		assertBDEquals(BigDecimal.valueOf(8), wage.getEveningHours());
		assertBDEquals(new BigDecimal("2"), wage.getOvertimeHours(new OvertimeClass(0, 2, new BigDecimal("1.25"))));
		assertBDEquals(new BigDecimal("2"), wage.getOvertimeHours(new OvertimeClass(2, 4, new BigDecimal("1.5"))));
		assertBDEquals(new BigDecimal("7"), wage.getOvertimeHours(new OvertimeClass(4, null, new BigDecimal("2"))));
		assertBDEquals(new BigDecimal("112.33"), wage.getWage().setScale(2, RoundingMode.HALF_UP));
	}
	
	@Test
	public void testTwoPersons() throws Exception {
		MonthlyWages wages = this.service.computeMonthlyWages(this.getClass().getResourceAsStream("twoPersons.csv"), WageSettings.DEFAULT);
		assertEquals(LocalDate.parse("2016-06-01"), wages.getMonth());
		
		assertEquals(2, wages.getWages().size());
		
		for (MonthlyWage wage : wages.getWages()) {
			Person person = wage.getPerson();
			if ("19".equals(person.getId())) {
				assertEquals("Jospel Kedettunen", person.getName());
				assertBDEquals(BigDecimal.valueOf(22), wage.getRegularHours());
				assertBDEquals(new BigDecimal("8.5"), wage.getEveningHours());
				assertBDEquals(new BigDecimal("0.5"), wage.getOvertimeHours(new OvertimeClass(0, 2, new BigDecimal("1.25"))));
				assertBDEquals(new BigDecimal("0"), wage.getOvertimeHours(new OvertimeClass(2, 4, new BigDecimal("1.5"))));
				assertBDEquals(new BigDecimal("0"), wage.getOvertimeHours(new OvertimeClass(4, null, new BigDecimal("2"))));
				assertBDEquals(new BigDecimal("94.62"), wage.getWage().setScale(2, RoundingMode.HALF_UP));
			}
			else if ("9".equals(person.getId())) {
				assertEquals("Erik Kulmajuhta", person.getName());
				assertBDEquals(BigDecimal.valueOf(24), wage.getRegularHours());
				assertBDEquals(new BigDecimal("0"), wage.getEveningHours());
				assertBDEquals(new BigDecimal("4.25"), wage.getOvertimeHours(new OvertimeClass(0, 2, new BigDecimal("1.25"))));
				assertBDEquals(new BigDecimal("0"), wage.getOvertimeHours(new OvertimeClass(2, 4, new BigDecimal("1.5"))));
				assertBDEquals(new BigDecimal("0"), wage.getOvertimeHours(new OvertimeClass(4, null, new BigDecimal("2"))));
				assertBDEquals(new BigDecimal("109.92"), wage.getWage().setScale(2, RoundingMode.HALF_UP));
			}
			else {
				fail("Invalid person id: "+person.getId());
			}
		}
	}
	
	@Test(expected=InvalidShiftListException.class)
	public void testInvalidInput() throws Exception {
		this.service.computeMonthlyWages(this.getClass().getResourceAsStream("no-data.csv"), WageSettings.DEFAULT);
	}
	
	@Test
	public void testInvalidDate() throws Exception {
		try {
			this.service.computeMonthlyWages(this.getClass().getResourceAsStream("invalid-date.csv"), WageSettings.DEFAULT);
			fail("No exception thrown!");
		}
		catch (InvalidShiftRecordException e) {
			assertEquals(1, e.getRecordNumber());
			assertEquals(ExceptionType.INVALID_DATE, e.getType());
		}
	}
	
	@Test
	public void testMissingDate() throws Exception {
		try {
			this.service.computeMonthlyWages(this.getClass().getResourceAsStream("missing-date.csv"), WageSettings.DEFAULT);
			fail("No exception thrown!");
		}
		catch (InvalidShiftRecordException e) {
			assertEquals(4, e.getRecordNumber());
			assertEquals(ExceptionType.INVALID_DATE, e.getType());
		}
	}
	
	@Test
	public void testMissingStart() throws Exception {
		try {
			this.service.computeMonthlyWages(this.getClass().getResourceAsStream("missing-start.csv"), WageSettings.DEFAULT);
			fail("No exception thrown!");
		}
		catch (InvalidShiftRecordException e) {
			assertEquals(6, e.getRecordNumber());
			assertEquals(ExceptionType.INVALID_START_TIME, e.getType());
		}
	}
	
	@Test
	public void testInvalidEnd() throws Exception {
		try {
			this.service.computeMonthlyWages(this.getClass().getResourceAsStream("invalid-end.csv"), WageSettings.DEFAULT);
			fail("No exception thrown!");
		}
		catch (InvalidShiftRecordException e) {
			assertEquals(4, e.getRecordNumber());
			assertEquals(ExceptionType.INVALID_END_TIME, e.getType());
		}
	}
	
	@Test
	public void testMissingPersonID() throws Exception {
		try {
			this.service.computeMonthlyWages(this.getClass().getResourceAsStream("missing-id.csv"), WageSettings.DEFAULT);
			fail("No exception thrown!");
		}
		catch (InvalidShiftRecordException e) {
			assertEquals(3, e.getRecordNumber());
			assertEquals(ExceptionType.MISSING_PERSON_ID, e.getType());
		}
	}
	
	@Test
	public void testMissingPersonName() throws Exception {
		try {
			this.service.computeMonthlyWages(this.getClass().getResourceAsStream("missing-name.csv"), WageSettings.DEFAULT);
			fail("No exception thrown!");
		}
		catch (InvalidShiftRecordException e) {
			assertEquals(2, e.getRecordNumber());
			assertEquals(ExceptionType.MISSING_PERSON_NAME, e.getType());
		}
	}
	
	@Test
	public void testInvalidNumberOfElements() throws Exception {
		try {
			this.service.computeMonthlyWages(this.getClass().getResourceAsStream("invalid-number-of-elements.csv"), WageSettings.DEFAULT);
			fail("No exception thrown!");
		}
		catch (InvalidShiftRecordException e) {
			assertEquals(4, e.getRecordNumber());
			assertEquals(ExceptionType.WRONG_NUMBER_OF_DATA_ELEMENTS, e.getType());
		}
	}
	
	/**
	 * Assert that the two given {@link BigDecimal}s represent equal numbers. This is needed because {@link BigDecimal#equals(Object)}
	 * also compares the numbers' scales and we don't want that here.
	 * @param expected
	 * @param actual
	 */
	private static void assertBDEquals(BigDecimal expected, BigDecimal actual) {
		assertTrue("Expected: "+expected+", got: "+actual, expected.compareTo(actual) == 0);
	}
}
