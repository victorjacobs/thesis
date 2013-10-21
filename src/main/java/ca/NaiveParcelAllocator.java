package ca;

import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.HashSet;
import java.util.Set;

/**
 * Naively allocates parcels to bids, only works when there are no conflicts. This is not enforced, weird results ensue
 * when trying to allocate conflicting bids.
 * Just to make sure the code infrastructure sort of works
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class NaiveParcelAllocator extends ParcelAllocator {

	@Override
	Set<Bid> solve() {
		for (Bid curBid : bids) {
			for (DefaultParcel par : curBid.getParcels()) {		// Oops O(n^2)
				if (!allocation.containsKey(par) || curBid.getBidValue() < allocation.get(par).getBidValue()) {
					allocation.put(par, curBid);
				}
			}
		}

		return new HashSet<Bid>(allocation.values());
	}

}
