package common;

import ca.CombAuctionBidder;
import com.google.common.base.Objects;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bid made by an agent
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Bid {

	private List<DefaultParcel> parcels;
	private double bidValue;
	private CombAuctionBidder bidder;

	public Bid(CombAuctionBidder bidder, final DefaultParcel parcel, double bidValue) {
		// NOTE double brace initialisation
		this(bidder, new ArrayList<DefaultParcel>() {{ add(parcel); }}, bidValue);
	}

	public Bid(CombAuctionBidder bidder, List<DefaultParcel> parcels, double bidValue) {
		this.parcels = parcels;
		this.bidValue = bidValue;
		this.bidder = bidder;
	}

	public double getBidValue() {
		return bidValue;
	}

	public List<DefaultParcel> getParcels() {
		return parcels;
	}

	public CombAuctionBidder getBidder() {
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

	// TODO
	@Override
	public int hashCode() {
		return Objects.hashCode(getBidValue(), getBidder(), getParcels());
	}
}
