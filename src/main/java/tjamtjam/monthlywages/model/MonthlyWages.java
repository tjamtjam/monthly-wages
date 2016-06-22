package tjamtjam.monthlywages.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class contains all the monthly wages for one month.
 *
 */
public class MonthlyWages {

	private LocalDate month;
	private Collection<MonthlyWage> wages;

	public MonthlyWages(LocalDate month, Collection<MonthlyWage> wages) {
		this.month = month;
		this.wages = wages;
	}

	public LocalDate getMonth() {
		return month;
	}

	public Collection<MonthlyWage> getWages() {
		if (this.wages == null) {
			this.wages = new ArrayList<>();
		}
		return wages;
	}
	
	/**
	 * @return Returns the {@link MonthlyWage}s from this class sorted by the person ids. The person ids are assumed
	 * to be numbers. If there are ids that are not numbers, those are sorted to the end of the collection.
	 */
	public SortedSet<MonthlyWage> getSortedWages() {
		SortedSet<MonthlyWage> sortedWages = new TreeSet<>((wage1, wage2) -> {
			Integer id1 = null, id2 = null;
			try {
				id1 = Integer.parseInt(wage1.getPerson().getId());
			}
			catch (NumberFormatException e) {}
			try {
				id2 = Integer.parseInt(wage2.getPerson().getId());
			}
			catch (NumberFormatException e) {}
			if (id1 != null && id2 != null) {
				return id1.compareTo(id2);
			}
			else if (id1 != null && id2 == null) {
				return -1;
			}
			else if (id1 == null && id2 != null) {
				return 1;
			}
			else {
				return wage1.getPerson().getId().compareTo(wage2.getPerson().getId());
			}
		});
		sortedWages.addAll(this.getWages());
		return sortedWages;
	}

}
