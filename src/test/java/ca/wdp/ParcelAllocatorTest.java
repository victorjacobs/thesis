package ca.wdp;

import common.truck.Bid;
import common.truck.Bidder;
import org.junit.Test;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		// TODO still something not completely ok here
		final int nbParcels = 6;
		final int bundleSize = 2;
		final int nbBundles = (int) Math.ceil(nbParcels / bundleSize);
		List<DefaultParcel> bundle;

		// Generate a lot of parcels
		List<DefaultParcel> parcels = new LinkedList<DefaultParcel>();
		DefaultParcel temp;
		for (int i = 0; i < nbParcels; i++) {
			temp = mock(DefaultParcel.class);
			when(temp.toString()).thenReturn(i + "");
			parcels.add(temp);
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

		Bidder b1 = mock(Bidder.class);
		Bidder b2 = mock(Bidder.class);

		// Create bids
		List<Bid<DefaultParcel>> bidSet1 = new LinkedList<Bid<DefaultParcel>>();
		for (List<DefaultParcel> b : bundleSet1) {
			bidSet1.add(new Bid<DefaultParcel>(b1, newLinkedHashSet(b), 20));
		}

		List<Bid<DefaultParcel>> bidSet2 = new LinkedList<Bid<DefaultParcel>>();
		for (List<DefaultParcel> b : bundleSet2) {
			bidSet2.add(new Bid<DefaultParcel>(b2, newLinkedHashSet(b), 10));
		}

		// Finally add everything to the allocator
		alloc.addAllBids(bidSet1);
		alloc.addAllBids(bidSet2);

		long start = System.currentTimeMillis();

		ParcelAllocation sol = alloc.solve();

		System.out.println("Running time " + Long.toString(System.currentTimeMillis() - start));

		assertTrue(sol.containsAll(bidSet2));
	}
}
