package ca;

import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents an allocation of parcels according to some set of bids. This basically is a solver for the WDP
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class ParcelAllocator {
	protected List<Bid> bids;
	protected Map<DefaultParcel, Bid> allocation;
	protected boolean finished = false;

	public ParcelAllocator() {
		allocation = new HashMap<DefaultParcel, Bid>();
		bids = new LinkedList<Bid>();	// Since sequential access (?)
	}

	public void addBid(Bid newBid) {
		bids.add(newBid);
	}

	public void addAllBids(List<Bid> newBids) {
		bids.addAll(newBids);
	}

	abstract boolean distributeParcels();
}
