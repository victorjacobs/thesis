package ca.wdp;

import common.truck.Bid;
import common.truck.Bidder;
import org.junit.Before;
import org.junit.Test;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 22/10/13
 * Time: 11:39
 */
public class BruteForceParcelAllocatorTest extends ParcelAllocatorTest {

	@Before
	public void setUp() {
		alloc = new BruteForceParcelAllocator();
	}

	/**
	 * p1  p2  p3  p4  p5
	 * @throws Exception
	 */
	@Test
	public void testSolve() throws Exception {
		DefaultParcel p1 = mock(DefaultParcel.class);
		DefaultParcel p2 = mock(DefaultParcel.class);
		DefaultParcel p3 = mock(DefaultParcel.class);
		DefaultParcel p4 = mock(DefaultParcel.class);
		DefaultParcel p5 = mock(DefaultParcel.class);

		List<DefaultParcel> bundle1 = new ArrayList<DefaultParcel>();
		bundle1.add(p1);
		bundle1.add(p2);

		List<DefaultParcel> bundle2 = new ArrayList<DefaultParcel>();
		bundle2.add(p3);
		bundle2.add(p4);
		bundle2.add(p5);

		List<DefaultParcel> bundle3 = new ArrayList<DefaultParcel>();
		bundle3.add(p1);

		List<DefaultParcel> bundle4 = new ArrayList<DefaultParcel>();
		bundle4.add(p2);
		bundle4.add(p3);

		List<DefaultParcel> bundle5 = new ArrayList<DefaultParcel>();
		bundle5.add(p4);
		bundle5.add(p5);

		Bid b1 = new Bid(mock(Bidder.class), newLinkedHashSet(bundle1), 10);
		Bid b2 = new Bid(mock(Bidder.class), newLinkedHashSet(bundle2), 20);
		Bid b3 = new Bid(mock(Bidder.class), newLinkedHashSet(bundle3), 5);
		Bid b4 = new Bid(mock(Bidder.class), newLinkedHashSet(bundle4), 5);
		Bid b5 = new Bid(mock(Bidder.class), newLinkedHashSet(bundle5), 5);

		alloc.addBid(b1);
		alloc.addBid(b2);
		alloc.addBid(b3);
		alloc.addBid(b4);
		alloc.addBid(b5);

		ParcelAllocation sol = alloc.solve();

		System.out.println();
//		assertEquals(5, sol.getValueOfParcel(p1), 0.000001f);
//		assertEquals(5, sol.getValueOfParcel(p1), 0.000001f); TODO
//		assertEquals(5, sol.getValueOfParcel(p1), 0.000001f);

//		assertFalse(sol.contains(b1));
//		assertFalse(sol.contains(b2));
//		assertTrue(sol.contains(b3));
//		assertTrue(sol.contains(b4));
//		assertTrue(sol.contains(b5));
	}

	@Test
	public void testPruning() {
		DefaultParcel p1 = mock(DefaultParcel.class);
		DefaultParcel p2 = mock(DefaultParcel.class);

		List<DefaultParcel> bundle1 = new ArrayList<DefaultParcel>();
		bundle1.add(p1);
		bundle1.add(p2);

		List<DefaultParcel> bundle2 = new ArrayList<DefaultParcel>();
		bundle2.add(p2);

		Bid b1 = new Bid(mock(Bidder.class), newLinkedHashSet(bundle1), 20);
		Bid b2 = new Bid(mock(Bidder.class), newLinkedHashSet(bundle2), 10);

		alloc.addBid(b1);
		alloc.addBid(b2);

//		Collection<Bid> sol = alloc.solve();	TODO
//
//		assertTrue(sol.contains(b1));
//		assertFalse(sol.contains(b2));
	}

}