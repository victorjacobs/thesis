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
		checkState(!communicators.isEmpty(), "there are no bidders..");

		final Iterator<ReAuctionBidder> it = communicators.iterator();
		Bid bestBid;
		ReAuctionBidder bestBidder = it.next();
		// if there are no other bidders, there is no need to organize an
		// auction at all (mainly used in test cases)
		if (it.hasNext()) {
			double bestValue = bestBidder.getBidFor(p, time);

			while (it.hasNext()) {
				final ReAuctionBidder cur = it.next();
				final double curValue = cur.getBidFor(p, time);
				if (curValue < bestValue) {
					bestValue = curValue;
					bestBidder = cur;
				}
			}
		}
		bestBidder.receiveParcel(p);
	}

	@Override
	public boolean register(ReAuctionBidder communicator) {
		// Make double binding happen
		communicator.register(this);

		return super.register(communicator);
	}

	/**
	 *
	 * @param par Parcel to re-auction
	 * @param reservationPrice Price other agents at least have to match for them to win the bid
	 * @return True if another agent won the bid at reservationPrice
	 */
	public boolean reAuction(DefaultParcel par, double reservationPrice) {
		System.out.println("tock");
		return true;
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
