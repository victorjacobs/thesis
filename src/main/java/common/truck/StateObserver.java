package common.truck;

import com.google.common.collect.ImmutableSet;
import rinde.sim.pdptw.common.DefaultParcel;

/**
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface StateObserver {
	public void notify(long time);
}
