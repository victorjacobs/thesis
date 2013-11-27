package common;

import rinde.sim.pdptw.common.DefaultParcel;

/**
 * Composites together a {@link rinde.sim.pdptw.common.DefaultParcel} and an {@link Auctioneer} to form a Parcel that can
 * handle changing of owners. This composite is preferred over simply extending {@link rinde.sim.pdptw.common.DefaultParcel}
 * since that's more messy.
 * TODO: this breaks transparency
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctionableParcel {

	private final Auctioneer auctioneer;
	private final DefaultParcel par;

	public ReAuctionableParcel(Auctioneer auctioneer, DefaultParcel par) {
		this.auctioneer = auctioneer;
		this.par = par;
	}

	public DefaultParcel getParcel() {
		return par;
	}

	public void changeOwner(long time) {
		auctioneer.auction(par, time);
	}
}
