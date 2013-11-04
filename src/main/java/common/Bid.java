package common;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Represents a bid made by an agent
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Bid implements Comparable<Bid> {

	private ImmutableList<DefaultParcel> parcels;
	private final double bidValue;
	private final Bidder bidder;
	private boolean parcelsReceived = false;

	public Bid(Bidder bidder, final DefaultParcel parcel, double bidValue) {
		// NOTE double brace initialisation
		this(bidder, new ArrayList<DefaultParcel>() {{ add(parcel); }}, bidValue);
	}

	public Bid(Bidder bidder, List<DefaultParcel> parcels, double bidValue) {
		this.parcels = ImmutableList.copyOf(parcels);
		this.bidValue = bidValue;
		this.bidder = bidder;
	}

	public double getBidValue() {
		return bidValue;
	}

	public ImmutableList<DefaultParcel> getParcels() {
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

	// TODO
	@Override
	public int hashCode() {
		return Objects.hashCode(getBidValue(), getBidder(), getParcels());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bid value " + getBidValue() + " for parcel [");

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
