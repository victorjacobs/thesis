package common.truck;

/**
 * Interface that allows for re-evaluating the state of a Truck and possibly changing it. It is called every
 * afterTick() on Truck with as argument the number of ticks since the last evaluation. If true is returned,
 * this indicates that the routine was run and the counter is reset.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface StateEvaluator {
	public boolean evaluateState(int ticksSinceLastCall, long time);
}
