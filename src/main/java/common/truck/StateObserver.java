package common.truck;

import com.google.common.collect.ImmutableSet;
import rinde.sim.pdptw.common.DefaultParcel;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 04/11/13
 * Time: 17:53
 */
public interface StateObserver {
	// TODO why send over new state? Normally in Observer, the observing object will take a look at the observed obj
	//	-> + SolverRoutePlanner already has reference to truck -> because the methods it needs are actually from the
	// RouteFollowingVehicle,
	//	-> Is this reasonable?
	public void notifyStateChanged(ImmutableSet<DefaultParcel> newState, long time);

	public boolean reEvaluateState(int ticksSinceLastCall, long time);
}
