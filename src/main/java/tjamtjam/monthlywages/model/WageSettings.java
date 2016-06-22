package tjamtjam.monthlywages.model;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * <p>This class is used to control how wages are computed from a shift list.</p>
 * <p>When constructing an instance of this class, all the settings should be provided.</p>
 * <p>{@link OvertimeClass}es defined for this class should be defined so that there are no gaps between
 * them (e.g. if there is a {@link OvertimeClass} ending at 2, there should be another {@link OvertimeClass}
 * starting at 2) and the first {@link OvertimeClass} should start at zero. The also should be an ending 
 * {@link OvertimeClass} with <code>null</code> ending. The {@link OvertimeClass}es also may not overlap each other. 
 * This is checked when adding new {@link OvertimeClass}es with {@link WageSettings#addOvertimeClass(OvertimeClass)}.</p>
 */
public class WageSettings {

	public static final WageSettings DEFAULT = new WageSettings()
			.withEveningHourlyAddition(new BigDecimal("1.15"))
			.withRegularHourlyWage(new BigDecimal("3.75"))
			.withOvertimeCutpoint(BigDecimal.valueOf(8))
			.withEveningStart(LocalTime.parse("18:00:00"))
			.withEveningEnd(LocalTime.parse("06:00:00"))
			.addOvertimeClass(new OvertimeClass(0, 2, new BigDecimal("1.25")))
			.addOvertimeClass(new OvertimeClass(2, 4, new BigDecimal("1.5")))
			.addOvertimeClass(new OvertimeClass(4, null, new BigDecimal("2")));
	
	private BigDecimal regularHourlyWage;
	private BigDecimal eveningHourlyAddition;
	private NavigableSet<OvertimeClass> overtimeClasses = new TreeSet<>();
	/**
	 * The time when evening pay starts.
	 */
	private LocalTime eveningStart;
	/**
	 * The time when evening pay ends.
	 */
	private LocalTime eveningEnd;
	/**
	 * After how many hours of work in one day, the rest of the hours should be considered overtime.
	 */
	private BigDecimal overtimeCutpoint;
	
	public WageSettings withRegularHourlyWage(BigDecimal wage) {
		this.regularHourlyWage = wage;
		return this;
	}
	
	public WageSettings withEveningHourlyAddition(BigDecimal addition) {
		this.eveningHourlyAddition = addition;
		return this;
	}
	
	public WageSettings withEveningStart(LocalTime eveningStart) {
		this.eveningStart = eveningStart;
		return this;
	}
	
	public WageSettings withEveningEnd(LocalTime eveningEnd) {
		this.eveningEnd = eveningEnd;
		return this;
	}
	
	public WageSettings withOvertimeCutpoint(BigDecimal overtimeCutpoint) {
		this.overtimeCutpoint = overtimeCutpoint;
		return this;
	}
	
	/**
	 * Adds a {@link OvertimeClass} to this object.
	 * @param overtimeClass
	 * @return
	 * @throws IllegalArgumentException If there is an overlap between the {@link OvertimeClass}es.
	 */
	public WageSettings addOvertimeClass(OvertimeClass overtimeClass) {
		for (OvertimeClass existing : this.overtimeClasses) {
			if (existing.overlaps(overtimeClass)) {
				throw new IllegalArgumentException("Overtime classes cannot overlap each other!");
			}
		}
		this.overtimeClasses.add(overtimeClass);
		return this;
	}

	public BigDecimal getRegularHourlyWage() {
		return regularHourlyWage;
	}

	public BigDecimal getEveningHourlyAddition() {
		return eveningHourlyAddition;
	}
	
	public NavigableSet<OvertimeClass> getOvertimeClasses() {
		return overtimeClasses;
	}

	/**
	 * 
	 * @return The time when the evening pay starts.
	 */
	public LocalTime getEveningStart() {
		return eveningStart;
	}

	/**
	 * 
	 * @return The time when the evening pay ends.
	 */
	public LocalTime getEveningEnd() {
		return eveningEnd;
	}

	/**
	 * @return The number of hours after the rest of the hours should be considered overtime.
	 */
	public BigDecimal getOvertimeCutpoint() {
		return overtimeCutpoint;
	}
}
