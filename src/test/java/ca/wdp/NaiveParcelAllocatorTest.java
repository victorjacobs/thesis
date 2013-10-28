package ca.wdp;

import common.Bid;
import common.Bidder;
import org.junit.Before;
import org.junit.Test;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 21/10/13
 * Time: 17:06
 */
public class NaiveParcelAllocatorTest extends ParcelAllocatorTest {
	@Before
	public void setUp() {
		alloc = new NaiveParcelAllocator();
	}

	@Test
	public void testAllocator() {
		DefaultParcel p1 = mock(DefaultParcel.class);
		DefaultParcel p2 = mock(DefaultParcel.class);
		DefaultParcel p3 = mock(DefaultParcel.class);

		List<DefaultParcel> bundle1 = new ArrayList<DefaultParcel>();
		bundle1.add(p1);
		bundle1.add(p2);

		List<DefaultParcel> bundle2 = new ArrayList<DefaultParcel>();
		bundle2.add(p3);

		Bidder bidder1 = mock(Bidder.class);
		Bidder bidder2 = mock(Bidder.class);

		Bid b1 = new Bid(bidder1, bundle1, 10);
		Bid b2 = new Bid(bidder2, bundle1, 20);

		alloc.addBid(b1);
		alloc.addBid(b2);

		ParcelAllocation sol = alloc.solve();
		assertEquals(10, sol.getValueOfParcel(p1), 0.00001d);
		assertEquals(10, sol.getValueOfParcel(p2), 0.00001d);
	}
}
