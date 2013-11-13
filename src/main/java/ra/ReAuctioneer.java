package ra;

import com.google.common.collect.ImmutableSet;
import common.Auctioneer;
import common.truck.StateObserver;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

/**
 * Handles parcel removal from the truck by sending it over to the auctioneer and re-auctioning it.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctioneer implements StateObserver {

	private Auctioneer auctioneer;

	public ReAuctioneer(Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
	}

	@Override
	public void notifyParcelAdded(ImmutableSet<DefaultParcel> newState, long time) {
		// Doesn't do anything on parcel added
	}

	@Override
	public void notifyParcelRemoved(DefaultParcel par, long time) {
		auctioneer.auction(par, time);
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
