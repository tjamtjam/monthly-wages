package tjamtjam.monthlywages.model;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import static org.junit.Assert.*;

import java.math.BigDecimal;

@RunWith(JUnit4.class)
public class OvertimeClassTest {

	@Test
	public void testOverlaps() {
		assertFalse(new OvertimeClass(0, 2, new BigDecimal("1.25")).overlaps(new OvertimeClass(2, null, new BigDecimal("1.5"))));
		assertFalse(new OvertimeClass(2, null, new BigDecimal("1.25")).overlaps(new OvertimeClass(0, 2, new BigDecimal("1.5"))));
		assertFalse(new OvertimeClass(0, 2, new BigDecimal("1.25")).overlaps(new OvertimeClass(2, 4, new BigDecimal("1.5"))));
		assertFalse(new OvertimeClass(2, 4, new BigDecimal("1.25")).overlaps(new OvertimeClass(0, 2, new BigDecimal("1.5"))));
		assertTrue(new OvertimeClass(2, 4, new BigDecimal("1.25")).overlaps(new OvertimeClass(0, 3, new BigDecimal("1.5"))));
		assertTrue(new OvertimeClass(2, null, new BigDecimal("1.25")).overlaps(new OvertimeClass(0, 3, new BigDecimal("1.5"))));
		assertTrue(new OvertimeClass(2, null, new BigDecimal("1.25")).overlaps(new OvertimeClass(0, null, new BigDecimal("1.5"))));
	}
	
}
