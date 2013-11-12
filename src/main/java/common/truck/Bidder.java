package common.truck;

import com.google.common.collect.ImmutableSet;
import common.Auctioneer;
import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import static com.google.common.base.Preconditions.checkState;

/**
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class Bidder {
	protected Truck truck;
	protected Auctioneer auctioneer;

	public void bindAuctioneer(Auctioneer auctioneer) {
		checkState(this.auctioneer == null);
		this.auctioneer = auctioneer;
	}

	public void bindTruck(Truck truck) {
		this.truck = truck;
	}

	public void receiveParcel(DefaultParcel par) {
		truck.addParcel(par);
	}

	public void receiveParcels(ImmutableSet<DefaultParcel> pars) {
		for (DefaultParcel par : pars) {
			receiveParcel(par);
		}
	}

	// TODO is this a good choice? Gives Bidder multiple responsibilities
	public void auction(DefaultParcel par, long time) {
		auctioneer.auction(par, time);
	}

	public abstract Bid getBidFor(DefaultParcel par, long time);

	//public abstract TODO add way to CA
}
