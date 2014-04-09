package ca.wdp;

import common.truck.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an allocation of parcels according to some set of bids. This basically is a solver for the WDP. This
 * class provides some helper methods for subclasses that implement the solve() method.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class ParcelAllocator {
	protected List<Bid> bids;

	public ParcelAllocator() {
		bids = new ArrayList<Bid>();
	}

	public void addBid(Bid newBid) {
		bids.add(newBid);
	}

	public void addAllBids(List<Bid> newBids) {
		bids.addAll(newBids);
	}

	public void distributeParcels() {
		solve().distributeParcels();
	}

	public boolean areAllParcelsAllocated(ParcelAllocation allocation) {
		Set<DefaultParcel> ret = new HashSet<DefaultParcel>();

		for (Bid b : bids) {
			ret.addAll(b.getParcels());
		}

		return ret.size() == allocation.getNbParcels();
	}

	/*
	 * For testing purposes set package visibility
	 */
	abstract ParcelAllocation solve();
}
