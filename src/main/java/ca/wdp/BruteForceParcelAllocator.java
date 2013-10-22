package ca.wdp;

import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.*;

/**
 * Implements a brute force, depth first WDP solver
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class BruteForceParcelAllocator extends ParcelAllocator {
	private Map<DefaultParcel, Set<Bid>> bins;

	public BruteForceParcelAllocator() {
		super();
		bins = new HashMap<DefaultParcel, Set<Bid>>();
	}

	@Override
	ParcelAllocation solve() {
		Collection<Bid> bestAllocation = null;
		double bestValue = Double.MAX_VALUE;

		// Prune all bids a that are contained by bid b and have a lower value
		List<Bid> prunedBids = new LinkedList<Bid>(bids);	// Copy list since we're removing things
		Bid bi, bj;

		for (int i = 0; i < bids.size(); i++) {
			bi = bids.get(i);

			for (int j = 0; j < bids.size(); j++) {
				if (i == j) continue;
				bj = bids.get(j);

				if (bj.contains(bi) && bi.getBidValue() < bj.getBidValue())
					prunedBids.remove(bi);
			}
		}


		// Now enumerate do a depth-first search of all possibilities
		int x = 0, y = prunedBids.size() - 1;
		Bid xBid, yBid = prunedBids.get(y);

		// Loop over goods
		// Select some bid for some good
		// Allocate it -> this spreads it out over the goods it belongs to
		// Recursive step next good
		// If good already allocated continue deeper
		// Backtrack etc

		fillBins();

		return null;
	}

	private Collection<Bid> solveRecursiveStep(Iterator<Map.Entry<DefaultParcel, Set<Bid>>> it) {
		// Base case
		if (!it.hasNext())
			return null;

		return null;
	}

	private void fillBins() {
		for (Bid b : bids) {
			for (DefaultParcel p : b.getParcels()) {
				if (!bins.containsKey(p)) {
					// Init bin
					bins.put(p, new HashSet<Bid>());
				}

				// Throw in bin
				bins.get(p).add(b);
			}
		}
	}

}
