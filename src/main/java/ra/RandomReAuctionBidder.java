package ra;

import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.util.SupplierRng;

import java.util.Iterator;

/**
 * Random re-auctioner:
 * <ul>
 *     <li>Re-evaluates parcels at random times</li>
 *     <li>Every time pick random parcel to re-auction</li>
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
		// Something's not quite right yet here, Iterators throw ConcurrentModificationExceptions when set is changed
		// between creating iterator and trying to remove it.
		Iterator<DefaultParcel> it = assignedParcels.iterator();
		DefaultParcel par;

		while (it.hasNext()) {
			par = it.next();

			if (rng.nextInt(10) < 1) {		// 10% chance of re-auctioning something
				System.out.println("I'm going to re-auction " + par.toString());
				// Temporary remove the parcel to allow for proper re-auctioning
				it.remove();

				commModel.reAuction(par);
				notifyChange();
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
