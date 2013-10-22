package ca.wdp;

import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Represents an allocation of parcels according to some set of bids. This basically is a solver for the WDP. This
 * class provides some helper methods for subclasses that implement the solve() method.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class ParcelAllocator {
	protected List<Bid> bids;
	protected Map<DefaultParcel, Bid> allocation;	// Might change this to protected

	private boolean finished = false;

	public ParcelAllocator() {
		allocation = new HashMap<DefaultParcel, Bid>();
		bids = new ArrayList<Bid>();
	}

	public void addBid(Bid newBid) {
		bids.add(newBid);
	}

	public void addAllBids(List<Bid> newBids) {
		bids.addAll(newBids);
	}

	public boolean distributeParcels() {
		checkState(!finished, "Parcels already distributed to bidders");

		for (Bid curBid : solve()) {
			curBid.getBidder().receiveParcels(curBid.getParcels());
		}

		finished = true;

		return false;
	}

	/**
	 * Internally allocates a bid to all parcels that are connected to it, doesn't check anything,
	 * that's up to the subclass to do in solve().
	 * @param b The bid being allocated
	 */
	protected void allocateBid(Bid b) {
		checkArgument(bids.contains(b));	// Bid passed should be in the allocator

		for (DefaultParcel p : allocation.keySet()) {
			if (b.getParcels().contains(p)) {
				allocation.put(p, b);
			}
		}
	}

	/**
	 * Checks whether a given bid is conflicting with the current allocation. I.e. there are parcels in the bid that
	 * are already in the allocation.
	 * @param b
	 * @return
	 */
	protected boolean conflictingBid(Bid b) {
		for (DefaultParcel p : b.getParcels()) {
			if (allocation.containsKey(p))
				return true;
		}

		return false;
	}

	protected double getValueOfCurrentAllocation() {
		double ret = 0;

		for (Bid b : allocation.values())
			ret += b.getBidValue();

		return ret;
	}

	/*
	 * For testing purposes set package visibility
	 */
	abstract Set<Bid> solve();
}
