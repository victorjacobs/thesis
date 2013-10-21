package ca;

import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.Collection;

/**
 * Naively allocates parcels to bids, only works when there are no conflicts
 * Just to make sure the code infrastructure sort of works
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class NaiveParcelAllocator extends ParcelAllocator {

	private Collection<Bid> solve() {
		for (Bid curBid : bids) {
			for (DefaultParcel par : curBid.getParcels()) {		// Oops O(n^2)
				if (!allocation.containsKey(par) || curBid.getBid() < allocation.get(par).getBid()) {
					allocation.put(par, curBid);
				}
			}
		}

		return allocation.values();
	}

	@Override
	public boolean distributeParcels() {
		if (finished)
			throw new IllegalStateException("NaiveParcelAllocator already distributed to bidders");

		for (Bid curBid : solve()) {
			curBid.getBidder().receiveParcels(curBid.getParcels());
		}

		return false;
	}

}
