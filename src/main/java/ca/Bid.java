package ca;

import rinde.sim.core.model.pdp.Parcel;

/**
 * Represents a bid made by an agent
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Bid {

	private Parcel parcel;
	private double bid;
	// Some link to bidder/agent?

	public Bid(Parcel parcel, double bid) {
		this.parcel = parcel;
		this.bid = bid;
	}

	public double getBid() {
		return bid;
	}

	public Parcel getParcel() {
		return parcel;
	}
}
