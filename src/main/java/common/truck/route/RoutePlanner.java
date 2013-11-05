package common.truck.route;

import common.truck.StateObserver;
import common.truck.Truck;
import rinde.logistics.pdptw.mas.route.AbstractRoutePlanner;

/**
 * Wrapper around {@link rinde.logistics.pdptw.mas.route.SolverRoutePlanner} to include the StateObserver interface.
 * This is needed to enforce that the route planners passed to Truck both inherit from the AbstractRoutePlanner and
 * implement the StateObserver.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class RoutePlanner extends AbstractRoutePlanner implements StateObserver {

	protected Truck truck;

	public void bindTruck(Truck truck) {
		this.truck = truck;
	}
}
