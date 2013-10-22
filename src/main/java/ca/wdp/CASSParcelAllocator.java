package ca.wdp;

import common.Bid;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements the Combinatorial Auction Structured Search (CASS) winner determination algorithm detailed in "Taming
 * the computational complexity of combinatorial auctions: Optimal and approximate approaches"
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class CASSParcelAllocator extends ParcelAllocator {

	@Override
	Collection<Bid> solve() {
		// Prune all bids a that are contained by bid b and have a lower value
		Set<Bid> prunedBidsList = new HashSet<Bid>();	// Don't need doubles
		Bid bi, bj;

		for (int i = 0; i < bids.size(); i++) {
			bi = bids.get(i);

			for (int j = i; j < bids.size(); j++) {
				bj = bids.get(j);

				if (bj.contains(bi) && bi.getBidValue() < bj.getBidValue())
					prunedBidsList.add(bi);
			}
		}

//		int x = 0, y = workingCopy.size() - 1;
//		Bid xBid, yBid = workingCopy.get(y);
//
//		while (y != 0) {
//
//			do {
//				xBid = workingCopy.get(x);
//				allocateBid(xBid);
//				x++;
//			} while (!conflictingBid(xBid));
//
//			// Update
//			if (getValueOfCurrentAllocation() < bestValue) {
//				bestValue = getValueOfCurrentAllocation();	// TODO two calls to get...()
//				bestAllocation = allocation.values();
//			}
//
//			while (y != 0) {
//				if (containsBid(yBid)) {
//					workingCopy.remove(y);
//					x = y + 1;
//				} else {
//					y--;
//				}
//			}
//		}



		return prunedBidsList;  //To change body of implemented methods use File | Settings | File Templates.
	}

}
