package ca.wdp;

import common.truck.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

/**
 * Naively allocates parcels to bids, only works when there are no conflicts. This is not enforced, weird results ensue
 * when trying to allocate conflicting bids.
 * Just to make sure the code infrastructure sort of works
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class NaiveParcelAllocator extends ParcelAllocator {

	@Override
	ParcelAllocation solve() {
		ParcelAllocation allocation = new ParcelAllocation();

		for (Bid<DefaultParcel> curBid : bids) {
			for (DefaultParcel par : curBid.getParcels()) {		// Oops O(n^2)
				if (!allocation.parcelExists(par) || curBid.getBidValue() < allocation.getValueOfParcel(par)) {
					allocation.allocateBid(curBid);
				}
			}
		}

		return allocation;
	}

}
