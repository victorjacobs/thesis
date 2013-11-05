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
	public void stateChanged(ImmutableSet<DefaultParcel> newState, long time);

	public boolean reEvaluateState(int ticksSinceLastCall, long time);
}
