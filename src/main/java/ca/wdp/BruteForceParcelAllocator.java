package ca.wdp;

import common.Bid;

import java.util.*;

/**
 * Implements a brute force, depth first WDP solver
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class BruteForceParcelAllocator extends ParcelAllocator {
	@Override
	Collection<Bid> solve() {
		Collection<Bid> bestAllocation = null;
		double bestValue = Double.MAX_VALUE;

		// TODO pruning broken
		// Prune all bids a that are contained by bid b and have a lower value
		Set<Bid> prunedBidsSet = new HashSet<Bid>();	// Don't need doubles
		Bid bi, bj;
//
//		for (int i = 0; i < bids.size(); i++) {
//			bi = bids.get(i);
//
//			for (int j = i; j < bids.size(); j++) {
//				bj = bids.get(j);
//
//				if (bj.contains(bi) && bi.getBidValue() < bj.getBidValue())
//					prunedBidsSet.add(bi);
//			}
//		}
//
//		// Convert to list for sequential access
//		List<Bid> workingCopy = new LinkedList<Bid>(prunedBidsSet);

		List<Bid> workingCopy = new LinkedList<Bid>(bids);

		for (int i = 0; i < workingCopy.size(); i++) {
			bi = workingCopy.get(i);
			allocateBid(bi);

			for (int j = 0; j < workingCopy.size(); j++) {
				if (i == j) continue;
				bj = workingCopy.get(j);

				if (!conflictingBid(bj))
					allocateBid(bj);
			}

			// Update value + enforce that all parcels are auctioned
			if (getValueOfCurrentAllocation() < bestValue && areAllParcelsAllocated()) {
				bestValue = getValueOfCurrentAllocation();
				bestAllocation = new LinkedList<Bid>(allocation.values());
			}

			resetAllocation();
		}

		return bestAllocation;  //To change body of implemented methods use File | Settings | File Templates.
	}

}
