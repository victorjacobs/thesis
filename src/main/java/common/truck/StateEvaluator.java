package common.truck;

import com.google.common.collect.ImmutableSet;
import rinde.sim.pdptw.common.DefaultParcel;

/**
 * Interface that allows for re-evaluation of the Truck's state. Every afterTick() shouldReEvaluate() is called to
 * see whether the evaluator wants to re-evaluate the state. If so, evaluateState() is called. This gets a immutable
 * copy of the Truck's state and then returns the parcels it wants removed/re-auctioned from the state. Whether a
 * certain parcel is allowed to be re-auctioned is checked in Truck itself. For now the StateEvaluator doesn't
 * receive a message when a re-auction has failed.
 * Note: re-auctioning follows naturally from the removing from the state since parcels can't just disappear.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class StateEvaluator {

	/**
	 * Re-evaluates given state and returns a set of parcels that should be removed and re-auctioned
	 *
	 * @param state
	 * @param time
	 * @return
	 */
	public abstract ImmutableSet<DefaultParcel> evaluateState(ImmutableSet<DefaultParcel> state, long time);

	public abstract boolean shouldReEvaluate(long ticks);

}
