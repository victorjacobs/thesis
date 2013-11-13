package common.truck;

import com.google.common.collect.ImmutableSet;
import rinde.sim.pdptw.common.DefaultParcel;

/**
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface StateObserver {
	// TODO this is not yet final -> might split up in special parcel added and parcel removed observers
	// TODO why send over new state? Normally in Observer, the observing object will take a look at the observed obj
	//	-> + SolverRoutePlanner already has reference to truck -> because the methods it needs are actually from the
	// RouteFollowingVehicle,
	//	-> Is this reasonable?
	public void notifyParcelAdded(ImmutableSet<DefaultParcel> newState, long time);

	public void notifyParcelRemoved(DefaultParcel par, long time);
}
