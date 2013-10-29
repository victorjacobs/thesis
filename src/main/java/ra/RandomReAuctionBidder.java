package ra;

import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.util.SupplierRng;

/**
 * Random re-auctioner:
 * <ul>
 *     <li>Re-evaluates parcels at random times</li>
 *     <li>Every time pick random parcel to re-auction</li>
 *     <li>For now don't use reservation price</li>
 * </ul>
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class RandomReAuctionBidder extends AbstractReAuctionBidder {

	@Override
	protected int getDelay() {
		return 50 + rng.nextInt(50);
	}

	@Override
	protected void reEvaluateParcels() {
		for (DefaultParcel par : assignedParcels) {
			if (rng.nextInt(10) < 1) {		// 10% chance of re-auctioning something
				System.out.println("I'm going to re-auction " + par.toString());

				if (commModel.reAuction(par, Double.MAX_VALUE)) {
					System.out.println("Auction succeeded");
					// Remove from parcels
					assignedParcels.remove(par);
					// Message the route planner to update schedule
					notifyChange();
				}

				return;
			}
		}
	}

	public static SupplierRng<RandomReAuctionBidder> supplier(final ObjectiveFunction objFunc) {
		return new SupplierRng.DefaultSupplierRng<RandomReAuctionBidder>() {
			@Override
			public RandomReAuctionBidder get(long seed) {
				return new RandomReAuctionBidder();
			}
		};
	}
}
