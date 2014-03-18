package common.truck;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSet;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * Represents a bid made by an agent
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Bid implements Comparable<Bid> {

	private ImmutableSet<DefaultParcel> parcels;
	private final double bidValue;
	private final Bidder bidder;
	private boolean parcelsReceived = false;

	public Bid(Bidder bidder, final DefaultParcel parcel, double bidValue) {
		// NOTE double brace initialisation
		this(bidder, newLinkedHashSet(Collections.singleton(parcel)), bidValue);
	}

	public Bid(Bidder bidder, Set<DefaultParcel> parcels, double bidValue) {
		this.parcels = ImmutableSet.copyOf(parcels);
		this.bidValue = bidValue;
		this.bidder = bidder;
	}

	public double getBidValue() {
		return bidValue;
	}

	public ImmutableSet<DefaultParcel> getParcels() {
		return parcels;
	}

	public Bidder getBidder() {
		return bidder;
	}

	/**
	 * Does this bid contain the parcels of an other bid o?
	 * @param o Other bid
	 * @return This bid contains all parcels in o
	 */
	public boolean contains(Bid o) {
		return parcels.containsAll(o.getParcels());
	}

	/**
	 * The bidder that made this bid, receives all parcels that this bid represents, this can happen only once
	 */
	public void receiveParcels() {
		checkState(!parcelsReceived, "Bidder already received parcels");
		bidder.receiveParcels(parcels);
		parcelsReceived = true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getBidValue(), getBidder(), getParcels());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bid value ").append(getBidValue()).append(" for parcel [");

		for (DefaultParcel p : parcels) {
			sb.append(p.toString());
			sb.append(" ");
		}

		sb.append("]");
		return sb.toString();
	}

	@Override
	public int compareTo(Bid that) {
		return ComparisonChain.start()
				.compare(this.bidValue, that.bidValue)
				.result();
	}
}
