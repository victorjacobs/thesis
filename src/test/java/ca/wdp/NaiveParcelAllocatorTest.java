package ca.wdp;

import ca.CombAuctionBidder;
import ca.wdp.NaiveParcelAllocator;
import common.Bid;
import org.junit.Before;
import org.junit.Test;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 21/10/13
 * Time: 17:06
 */
public class NaiveParcelAllocatorTest {
	private NaiveParcelAllocator alloc;

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

		CombAuctionBidder bidder1 = mock(CombAuctionBidder.class);
		CombAuctionBidder bidder2 = mock(CombAuctionBidder.class);

		Bid b1 = new Bid(bidder1, bundle1, 10);
		Bid b2 = new Bid(bidder2, bundle1, 20);

		alloc.addBid(b1);
		alloc.addBid(b2);

		Collection<Bid> sol = alloc.solve();
		assertTrue(sol.contains(b1));
		assertFalse(sol.contains(b2));
	}
}
