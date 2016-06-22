package tjamtjam.monthlywages.model;

import java.math.BigDecimal;

/**
 * An {@link OvertimeClass} tells when (between which overtime hours) and what is the overtime wage multiplier for
 * those hours.
 *
 */
public class OvertimeClass implements Comparable<OvertimeClass> {

	/**
	 * At which hour this overtime class starts.
	 */
	private int startHour;
	/**
	 * At which hour this overtime class ends. If this is <code>null</code>, this overtime class does not end.
	 */
	private Integer endHour;
	/**
	 * The multiplier when used to compute the overtime pay.
	 */
	private BigDecimal multiplier;

	public OvertimeClass(int startHour, Integer endHour, BigDecimal multiplier) {
		this.startHour = startHour;
		this.endHour = endHour;
		this.multiplier = multiplier;
	}

	/**
	 * @return At which hour this overtime class starts.
	 */
	public int getStartHour() {
		return startHour;
	}

	/**
	 * @return At which hour this overtime class ends. If this is <code>null</code>, this overtime class does not end.
	 */
	public Integer getEndHour() {
		return endHour;
	}

	/**
	 * @return The multiplier when used to compute the overtime pay.
	 */
	public BigDecimal getMultiplier() {
		return multiplier;
	}
	
	/**
	 * @param anotherClass
	 * @return <code>true</code> if this and the given {@link OvertimeClass} overlap. I.e. one of the classes start
	 * before the other has ended.
	 */
	public boolean overlaps(OvertimeClass anotherClass) {
		if (this.compareTo(anotherClass) > 0) {
			return anotherClass.overlaps(this);
		}
		if (this.endHour == null) {
			return anotherClass.endHour == null || anotherClass.endHour.intValue() > this.startHour;
		}
		else {
			if (anotherClass.endHour == null) {
				return this.endHour.intValue() > anotherClass.startHour;
			}
			else {
				return anotherClass.startHour < this.endHour;
			}
		}
	}

	@Override
	public int compareTo(OvertimeClass o) {
		return Integer.compare(this.startHour, o.startHour);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endHour == null) ? 0 : endHour.hashCode());
		result = prime * result + ((multiplier == null) ? 0 : multiplier.hashCode());
		result = prime * result + startHour;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OvertimeClass other = (OvertimeClass) obj;
		if (endHour == null) {
			if (other.endHour != null)
				return false;
		}
		else if (!endHour.equals(other.endHour))
			return false;
		if (multiplier == null) {
			if (other.multiplier != null)
				return false;
		}
		else if (!multiplier.equals(other.multiplier))
			return false;
		if (startHour != other.startHour)
			return false;
		return true;
	}

}
