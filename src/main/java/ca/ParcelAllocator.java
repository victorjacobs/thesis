package ca;

import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.*;

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

	public boolean distributeParcels() {
		if (finished)
			throw new IllegalStateException("NaiveParcelAllocator already distributed to bidders");

		for (Bid curBid : solve()) {
			curBid.getBidder().receiveParcels(curBid.getParcels());
		}

		return false;
	}

	/*
	 * For testing purposes set package visibility
	 */
	abstract Set<Bid> solve();
}
