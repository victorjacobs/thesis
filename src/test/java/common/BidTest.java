package common;

import org.junit.Test;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 22/10/13
 * Time: 09:41
 */
public class BidTest {

	@Test
	public void testContains() throws Exception {
		DefaultParcel p1 = mock(DefaultParcel.class);
		DefaultParcel p2 = mock(DefaultParcel.class);
		DefaultParcel p3 = mock(DefaultParcel.class);

		List<DefaultParcel> bundle1 = new ArrayList<DefaultParcel>();
		bundle1.add(p1);
		bundle1.add(p2);
		List<DefaultParcel> bundle2 = new ArrayList<DefaultParcel>();
		bundle2.add(p1);

		Bid b1 = new Bid(mock(Bidder.class), bundle1, 20);
		Bid b2 = new Bid(mock(Bidder.class), bundle2, 20);

		assertTrue(b1.contains(b2));
		assertFalse(b2.contains(b1));

		// Partly overlapping
		List<DefaultParcel> bundle3 = new ArrayList<DefaultParcel>();
		bundle3.add(p2);
		bundle3.add(p3);

		Bid b3 = new Bid(mock(Bidder.class), bundle3, 20);

		assertFalse(b3.contains(b1));
		assertFalse(b1.contains(b3));
	}
}
