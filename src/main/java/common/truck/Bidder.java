package common.truck;

import common.Auctioneer;
import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.Set;

/**
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class Bidder {
	protected ReAuctionTruck truck;
	protected final Auctioneer auctioneer;

	public Bidder(Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
		auctioneer.registerBidder(this);
	}

	public void bindTruck(ReAuctionTruck truck) {
		this.truck = truck;
	}

	public void receiveParcel(DefaultParcel par) {
		truck.addParcel(par);
	}

	public void receiveParcels(Set<DefaultParcel> pars) {
		for (DefaultParcel par : pars) {
			truck.addParcel(par);
		}
	}

	public abstract Bid getBidFor(DefaultParcel par, long time);

	//public abstract TODO add way to CA
}
