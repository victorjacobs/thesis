package common.truck;

import com.google.common.collect.ImmutableSet;
import common.auctioning.ReAuctionableParcel;
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
	private Truck truck;

	/**
	 * Re-evaluates given state and returns a set of parcels that should be removed and re-auctioned
	 * @param time Simulator time
	 * @return Set of parcels that the StateEvaluator wants removed from the truck's state.
	 */
	public abstract ImmutableSet<DefaultParcel> evaluateState(long time);

	/**
	 * Returns whether or not the StateEvaluator should be run. This is evaluated every
	 * {@link Truck#afterTick(rinde.sim.core.TimeLapse)}.
	 * @param ticks Number of ticks since start of simulation
	 * @return True if the StateEvaluator should be executed
	 */
	public abstract boolean shouldReEvaluate(long ticks);

	public void setTruck(Truck truck) {
		this.truck = truck;
	}

	protected Truck getTruck() {
		return truck;
	}
}
