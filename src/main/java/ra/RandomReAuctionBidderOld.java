package ra;

import rinde.logistics.pdptw.mas.Truck;
import rinde.logistics.pdptw.mas.route.SolverRoutePlanner;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.util.SupplierRng;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Random re-auctioner:
 * <ul>
 *     <li>Re-evaluates parcels at random times</li>
 *     <li>Every time pick random parcel to re-auction</li>
 * </ul>
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
@Deprecated
public class RandomReAuctionBidderOld extends AbstractReAuctionBidder {

	@Override
	protected int getDelay() {
		return 30 + rng.nextInt(50);
	}


	@Override
	protected void reEvaluateParcels() {
		if (assignedParcels.isEmpty())
			return;

		int i = rng.nextInt(assignedParcels.size() * 3) + 3;	// Arbitrary constant 3 is arbitrary

		if (i >= assignedParcels.size())
			return;

		DefaultParcel par = (new LinkedList<DefaultParcel>(assignedParcels)).get(i);

		System.out.println("[" + this.toString() + "] I'm going to re-auction " + par.toString());

		assignedParcels.remove(par);
		Queue<DefaultParcel> newRoute = new LinkedList(((Truck) vehicle.get()).getRoute());
		newRoute.removeAll(Collections.singleton(par));
		for (DefaultParcel cur : newRoute) {
			if (!pdpModel.get().getParcelState(cur).isPickedUp()) {
				assignedParcels.add(cur);
			}
		}

		((SolverRoutePlanner) ((Truck) vehicle.get()).getRoutePlanner()).changeRoute(newRoute);
		((Truck) vehicle.get()).getRoutePlanner().update(assignedParcels, 0);
		((Truck) vehicle.get()).setRoute(((Truck) vehicle.get()).getRoutePlanner().currentRoute().get());

		commModel.reAuction(par);
	}

	//@Override
	protected void reEvaluateParcelsOld() {
		if (assignedParcels.isEmpty())
			return;

		// Something's not quite right yet here, Iterators throw ConcurrentModificationExceptions when set is changed
		// between creating iterator and trying to remove it.
		Iterator<DefaultParcel> it = assignedParcels.iterator();
		DefaultParcel par = null;
		Truck thisTruck = ((Truck) vehicle.get());
		// Don't try to re-auction potential next hop in route. This can be disastrous when already something
		DefaultParcel nextParInRoute = thisTruck.getRoute().size() != 0 ?
				thisTruck.getRoute().iterator().next() : null;

		int i = 0;

		while (it.hasNext()) {
			if (i > assignedParcels.size() / 2 && rng.nextInt(10) <= 1) {
				par = it.next();
				break;
			} else {
				it.next();
			}

			i++;
		}

		if (par == null || par == nextParInRoute)
			return;

		System.out.println("[" + this.toString() + "] I'm going to re-auction " + par.toString());
		// Temporary remove the parcel to allow for proper re-auctioning
		assignedParcels.remove(par);
		// Also remove parcel from current route since weird stuff happens otherwise in ArraysSolvers
//		List<DefaultParcel> newRoute = new LinkedList(thisTruck.getRoute());
//		newRoute.removeAll(Collections.singleton(par));
//		thisTruck.setRoute(newRoute);

		commModel.reAuction(par);
	}

	public static SupplierRng<RandomReAuctionBidderOld> supplier(final ObjectiveFunction objFunc) {
		return new SupplierRng.DefaultSupplierRng<RandomReAuctionBidderOld>() {
			@Override
			public RandomReAuctionBidderOld get(long seed) {
				return new RandomReAuctionBidderOld();
			}
		};
	}
}
