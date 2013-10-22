package ca.wdp;

import ca.CombAuctionBidder;
import common.Bid;
import org.junit.Before;
import org.junit.Test;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 22/10/13
 * Time: 09:38
 */
public class CASSParcelAllocatorTest extends ParcelAllocatorTest {

	@Before
	public void setUp() {
		alloc = new CASSParcelAllocator();
	}

	@Test
	public void testSolve() throws Exception {
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

		Bid b1 = new Bid(bidder1, bundle1, 20);
		Bid b2 = new Bid(bidder2, bundle2, 10);

		alloc.addBid(b1);
		alloc.addBid(b2);

//		Collection<Bid> sol = alloc.solve();	TODO
//		assertTrue(sol.contains(b2));
//		assertFalse(sol.contains(b1));
	}
}
