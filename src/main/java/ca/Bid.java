package ca;

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
	private double bid;
	private CombAuctionBidder bidder;

	public Bid(CombAuctionBidder bidder, final DefaultParcel parcel, double bid) {
		// NOTE double brace initialisation
		this(bidder, new ArrayList<DefaultParcel>() {{ add(parcel); }}, bid);
	}

	public Bid(CombAuctionBidder bidder, List<DefaultParcel> parcels, double bid) {
		this.parcels = parcels;
		this.bid = bid;
		this.bidder = bidder;
	}

	public double getBid() {
		return bid;
	}

	public List<DefaultParcel> getParcels() {
		return parcels;
	}

	public CombAuctionBidder getBidder() {
		return bidder;
	}
}
