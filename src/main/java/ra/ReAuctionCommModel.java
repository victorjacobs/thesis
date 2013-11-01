package ra;

import common.Bid;
import rinde.logistics.pdptw.mas.comm.AbstractCommModel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkState;

/**
 * Communication model that supports re-auctioning. ReAuctionBidders are doubly bound to this model so they can call
 * the reAuction method.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctionCommModel extends AbstractCommModel<ReAuctionBidder> {

	@Override
	protected void receiveParcel(DefaultParcel p, long time) {
		bestBid(p, time).receiveParcels();
	}

	/**
	 * TODO reservation price not really needed when just re-auctioning again because agent that started the
	 * re-auction will just win the auction again
	 *
	 * @param par Parcel to re-auction
	 * @return True if another agent won the bid at reservationPrice
	 */
	public void reAuction(DefaultParcel par) {
		Bid bid = bestBid(par, 0);	// TODO what about time? + this method is the same as receiveparcel
		System.out.println("[" + bid.getBidder().toString() + "] wins re-auction");

		bid.receiveParcels();
	}

	private Bid bestBid(DefaultParcel p, long time) {
		checkState(!communicators.isEmpty(), "there are no bidders..");

		final Iterator<ReAuctionBidder> it = communicators.iterator();
		Bid bestBid  = it.next().getBidFor(p, time);
		Bid curBid;
		while (it.hasNext()) {
			final ReAuctionBidder cur = it.next();
			curBid = cur.getBidFor(p, time);
			if (curBid.compareTo(bestBid) < 0) {
				bestBid = curBid;
			}
		}

		return bestBid;
	}

	@Override
	public boolean register(ReAuctionBidder communicator) {
		// Make double binding happen
		communicator.register(this);

		return super.register(communicator);
	}

	public static SupplierRng<ReAuctionCommModel> supplier() {
		return new SupplierRng.DefaultSupplierRng<ReAuctionCommModel>() {
			@Override
			public ReAuctionCommModel get(long seed) {
				return new ReAuctionCommModel();
			}
		};
	}
}
