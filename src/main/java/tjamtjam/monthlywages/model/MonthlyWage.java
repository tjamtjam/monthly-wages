package tjamtjam.monthlywages.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all the data needed to compute the monthly wage for one person.
 * @author tjamtjam
 *
 */
public class MonthlyWage {

	private Person person;
	private WageSettings settings;
	private Map<LocalDate, DailyWorkingHours> dailyWorkingHours = new HashMap<>();
	
	public MonthlyWage(Person person, WageSettings settings) {
		this.person = person;
		this.settings = settings;
	}

	public Person getPerson() {
		return person;
	}

	public BigDecimal getRegularHours() {
		return this.dailyWorkingHours.values().stream().map(DailyWorkingHours::getRegularHours)
				.reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
	}

	public BigDecimal getEveningHours() {
		return this.dailyWorkingHours.values().stream().map(DailyWorkingHours::getEveningHours)
				.reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
	}

	public BigDecimal getOvertimeHours(OvertimeClass overtimeClass) {
		return this.dailyWorkingHours.values().stream().map(dwh -> dwh.getOvertimeHours(overtimeClass))
				.reduce(BigDecimal.ZERO, (a,b) -> a.add(b));
	}
	
	public void addWageRecord(ShiftRecord record) {
		DailyWorkingHours hours = this.dailyWorkingHours.get(record.getDate());
		if (hours == null) {
			hours = new DailyWorkingHours(this.settings);
			this.dailyWorkingHours.put(record.getDate(), hours);
		}
		hours.addShiftRecord(record);
	}
	
	public BigDecimal getWage() {
		BigDecimal regularHours = this.getRegularHours();
		BigDecimal eveningHours = this.getEveningHours();
		BigDecimal wage = regularHours.multiply(this.settings.getRegularHourlyWage());
		wage = wage.add(eveningHours.multiply(this.settings.getEveningHourlyAddition()));
		for (OvertimeClass overtimeClass : this.settings.getOvertimeClasses()) {
			BigDecimal overtimeHours = this.getOvertimeHours(overtimeClass);
			wage = wage.add(overtimeHours.multiply(this.settings.getRegularHourlyWage().multiply(overtimeClass.getMultiplier())));
		}
		return wage;
	}
}
