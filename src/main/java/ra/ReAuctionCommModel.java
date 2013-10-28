package ra;

import rinde.logistics.pdptw.mas.comm.AbstractCommModel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

/**
 * Communication model that supports re-auctioning. ReAuctionBidders are doubly bound to this model so they can call
 * the reAuction method.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctionCommModel extends AbstractCommModel<ReAuctionBidder> {

	@Override
	protected void receiveParcel(DefaultParcel p, long time) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean register(ReAuctionBidder communicator) {
		// Make double binding happen
		communicator.register(this);

		return super.register(communicator);
	}

	public boolean reAuction(DefaultParcel par, double reservationPrice) {
		System.out.println("tock");
		return false;
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
