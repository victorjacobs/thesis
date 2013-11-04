package common;

import common.truck.Bidder;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO binding with model
public class Auctioneer {
	private Set<Bidder> bidders;


	public void auction(DefaultParcel par) {
		auction(par, 0);
	}

	public void auction(DefaultParcel par, double reservationPrice) {
		// TODO get time from somewhere
		checkState(!bidders.isEmpty(), "there are no bidders..");

		final Iterator<Bidder> it = bidders.iterator();
		Bid bestBid  = it.next().getBidFor(par, 0);
		Bid curBid;
		while (it.hasNext()) {
			final Bidder cur = it.next();
			curBid = cur.getBidFor(par, 0);
			if (curBid.compareTo(bestBid) < 0) {
				bestBid = curBid;
			}
		}

		bestBid.receiveParcels();
	}

	public void registerBidder(Bidder bidder) {
		bidders.add(bidder);
	}
}
