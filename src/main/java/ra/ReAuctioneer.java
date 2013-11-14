package ra;

import common.truck.Bidder;
import common.truck.ParcelRemoveHandler;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

/**
 * Handles parcel removal from the truck by sending it over to the auctioneer and re-auctioning it.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctioneer implements ParcelRemoveHandler {

	private Bidder bidder;

	public ReAuctioneer(Bidder bidder) {
		this.bidder = bidder;
	}

	@Override
	public void handleParcelRemove(DefaultParcel par, long time) {
		bidder.auction(par, time);
	}

	// TODO
	public static SupplierRng<ReAuctioneer> supplier() {
		return new SupplierRng.DefaultSupplierRng<ReAuctioneer>() {
			@Override
			public ReAuctioneer get(long seed) {
				return new ReAuctioneer(null);
			}
		};
	}
}
