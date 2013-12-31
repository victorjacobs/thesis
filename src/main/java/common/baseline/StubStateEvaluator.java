package common.baseline;

import com.google.common.collect.ImmutableSet;
import common.truck.StateEvaluator;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class StubStateEvaluator extends StateEvaluator {

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(long time) {
		return null;
	}

	@Override
	public boolean shouldReEvaluate(long ticks) {
		return false;
	}

	public static SupplierRng<? extends StateEvaluator> supplier() {
		return new SupplierRng.DefaultSupplierRng<StubStateEvaluator>() {

			@Override
			public StubStateEvaluator get(long seed) {
				return new StubStateEvaluator();
			}
		};
	}
}
