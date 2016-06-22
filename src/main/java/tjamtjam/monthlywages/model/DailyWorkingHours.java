package tjamtjam.monthlywages.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains working hours for one day and one person.
 *
 */
public class DailyWorkingHours {

	private BigDecimal regularHours = BigDecimal.ZERO;
	private BigDecimal eveningHours = BigDecimal.ZERO;
	private Map<OvertimeClass, BigDecimal> overtimeHours = new HashMap<>();
	private WageSettings settings;
	private BigDecimal totalHours = BigDecimal.ZERO;
	
	public DailyWorkingHours(WageSettings settings) {
		this.settings = settings;
	}

	public BigDecimal getOvertimeHours(OvertimeClass overtimeClass) {
		BigDecimal hours = this.overtimeHours.get(overtimeClass);
		return hours == null ? BigDecimal.ZERO : hours;
	}
	
	public void setOvertimeHours(BigDecimal overtimeHours) {
		this.overtimeHours = new HashMap<>();
		if (overtimeHours.compareTo(BigDecimal.ZERO) > 0) {
			for (OvertimeClass overtimeClass : this.settings.getOvertimeClasses()) {
				if (overtimeClass.getEndHour() == null) {
					this.overtimeHours.put(overtimeClass, overtimeHours);
				}
				else {
					BigDecimal overtimeClassHours = BigDecimal.valueOf(overtimeClass.getEndHour() - overtimeClass.getStartHour());
					BigDecimal hours = overtimeClassHours.min(overtimeHours);
					this.overtimeHours.put(overtimeClass, hours);
					overtimeHours = overtimeHours.subtract(hours);
				}
			}
		}
	}
	
	public void addToEveningHours(BigDecimal hours) {
		this.eveningHours = this.eveningHours.add(hours);
	}

	public BigDecimal getRegularHours() {
		return regularHours;
	}

	public void setRegularHours(BigDecimal regularHours) {
		this.regularHours = regularHours;
	}

	public BigDecimal getEveningHours() {
		return eveningHours;
	}
	
	public void addShiftRecord(ShiftRecord record) {
		this.totalHours = this.totalHours.add(record.getTotalHours());
		if (this.totalHours.compareTo(BigDecimal.valueOf(8)) > 0) {
			this.setOvertimeHours(this.totalHours.subtract(BigDecimal.valueOf(8)));
			this.regularHours = BigDecimal.valueOf(8);
		}
		else {
			this.regularHours = this.totalHours;
		}
		this.addToEveningHours(record.getEveningHours(settings.getEveningStart(), settings.getEveningEnd()));
	}
}
