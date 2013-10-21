package ca;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an allocation of parcels according to some set of bids. This basically is a solver for the WDP
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class NaiveParcelAllocator implements ParcelAllocator {

	private List<Bid> bids;
	private boolean finished = false;

	public NaiveParcelAllocator() {
		bids = new ArrayList<Bid>();
	}

	@Override
	public void addBid(Bid newBid) {
		bids.add(newBid);
	}

	@Override
	public void addAllBids(List<Bid> newBids) {
		bids.addAll(newBids);
	}

	private List<Bid> solve() {

		return null;
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
