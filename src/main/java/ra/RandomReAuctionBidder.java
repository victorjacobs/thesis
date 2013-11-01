package ra;

import rinde.logistics.pdptw.mas.Truck;
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
		DefaultParcel par = null;
		// Don't try to re-auction potential next hop in route. This can be disastrous when already something
		DefaultParcel nextParInRoute = ((Truck) vehicle.get()).getRoute().size() != 0 ?
				((Truck) vehicle.get()).getRoute().iterator().next() : null;

		while (it.hasNext()) {
			if (rng.nextInt(10) <= 1) {
				par = it.next();
				break;
			} else {
				it.next();
			}
		}

		if (par == null || par == nextParInRoute)
			return;

		System.out.println("[" + this.toString() + "] I'm going to re-auction " + par.toString());
		// Temporary remove the parcel to allow for proper re-auctioning
		assignedParcels.remove(par);

		commModel.reAuction(par);
		notifyChange();
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
