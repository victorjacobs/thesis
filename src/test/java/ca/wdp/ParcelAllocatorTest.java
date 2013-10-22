package ca.wdp;

import ca.CombAuctionBidder;
import common.Bid;
import org.junit.Test;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 22/10/13
 * Time: 11:40
 */
public abstract class ParcelAllocatorTest {
	protected ParcelAllocator alloc;

	@Test
	public void stressTestWithTimings() {
		final int nbParcels = 6;
		final int bundleSize = 2;
		final int nbBundles = (int) Math.ceil(nbParcels / bundleSize);
		List<DefaultParcel> bundle;

		// Generate a lot of parcels
		List<DefaultParcel> parcels = new LinkedList<DefaultParcel>();
		for (int i = 0; i < nbParcels; i++) {
			parcels.add(mock(DefaultParcel.class));
		}

		// Generate two sets of bundles, shifted by one so they all overlap (worst case)
		List<List<DefaultParcel>> bundleSet1 = new LinkedList<List<DefaultParcel>>();
		for (int i = 0; i < nbBundles; i++) {
			bundle = new LinkedList<DefaultParcel>();
			for (int j = i * bundleSize; j < (i + 1) * bundleSize; j++) {
				bundle.add(parcels.get(j));
			}
			bundleSet1.add(bundle);
		}

		// Shifted by 1
		List<List<DefaultParcel>> bundleSet2 = new LinkedList<List<DefaultParcel>>();
		List<DefaultParcel> firstBundle = new LinkedList<DefaultParcel>();
		firstBundle.add(parcels.get(0));

		List<DefaultParcel> lastBundle = new LinkedList<DefaultParcel>();
		lastBundle.add(parcels.get(parcels.size() - 1));

		bundleSet2.add(firstBundle);
		bundleSet2.add(lastBundle);

		for (int i = 0; i < nbBundles - 1; i++) {
			bundle = new LinkedList<DefaultParcel>();
			for (int j = i * bundleSize; j < (i + 1) * bundleSize; j++) {
				bundle.add(parcels.get(j + 1));		// Shift
			}
			bundleSet2.add(bundle);
		}

		CombAuctionBidder b1 = mock(CombAuctionBidder.class);
		CombAuctionBidder b2 = mock(CombAuctionBidder.class);

		// Create bids
		List<Bid> bidSet1 = new LinkedList<Bid>();
		for (List<DefaultParcel> b : bundleSet1) {
			bidSet1.add(new Bid(b1, b, 20));
		}

		List<Bid> bidSet2 = new LinkedList<Bid>();
		for (List<DefaultParcel> b : bundleSet2) {
			bidSet2.add(new Bid(b2, b, 10));
		}

		// Finally add everything to the allocator
		alloc.addAllBids(bidSet1);
		alloc.addAllBids(bidSet2);

		long start = System.currentTimeMillis();

		ParcelAllocation sol = alloc.solve();

		System.out.println("Running time " + Long.toString(System.currentTimeMillis() - start));

		//assertTrue(sol.containsAll(bidSet2)); TODO
	}
}
