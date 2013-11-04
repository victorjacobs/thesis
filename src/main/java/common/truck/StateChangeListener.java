package common.truck;

import com.google.common.collect.ImmutableSet;
import rinde.sim.pdptw.common.DefaultParcel;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 04/11/13
 * Time: 17:53
 */
public interface StateChangeListener {
	public void notifyStateChanged(ImmutableSet<DefaultParcel> newState, long time);
}
