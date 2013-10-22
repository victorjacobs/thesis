package ca.wdp;

import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Class representing an allocation of bids to parcels.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ParcelAllocation {
	private Map<DefaultParcel, Bid> allocation;	// Might change this to protected

	public ParcelAllocation() {
		this.allocation = new HashMap<DefaultParcel, Bid>();
	}

	/**
	 * Internally allocates a bid to all parcels that are connected to it, doesn't check anything,
	 * that's up to the subclass to do in solve().
	 * @param b The bid being allocated
	 */
	public void allocateBid(Bid b) {
		for (DefaultParcel p : b.getParcels()) {
			allocation.put(p, b);
		}
	}

	/**
	 * Checks whether a given bid is conflicting with the current allocation. I.e. there are parcels in the bid that
	 * are already in the allocation.
	 * @param b
	 * @return
	 */
	public boolean conflictingBid(Bid b) {
		for (DefaultParcel p : b.getParcels()) {
			if (allocation.containsKey(p))
				return true;
		}

		return false;
	}

	public boolean containsBid(Bid b) {
		for (DefaultParcel p : b.getParcels()) {
			if (!allocation.containsKey(p))
				return false;
		}

		return true;
	}

	/**
	 * Evaluates the current allocation of parcels.
	 * @return
	 */
	public double getValue() {
		double ret = 0;

		for (Bid b : new HashSet<Bid>(allocation.values()))
			ret += b.getBidValue();

		return ret;
	}

	public double getValueOfParcel(DefaultParcel p) {
		checkArgument(allocation.containsKey(p));

		return allocation.get(p).getBidValue();
	}

	public boolean parcelExists(DefaultParcel p) {
		return allocation.containsKey(p);
	}

	public int getNbParcels() {
		return allocation.keySet().size();
	}

	public void distributeParcels() {
		for (Bid b : allocation.values()) {
			b.receiveParcels();
		}
	}
}
