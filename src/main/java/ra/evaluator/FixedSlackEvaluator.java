package ra.evaluator;

import com.google.common.collect.ImmutableSet;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Map;

/**
 * This state evaluator goes over all the parcels in the state and computes the slack for every one.
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class FixedSlackEvaluator extends SlackEvaluator {
    public FixedSlackEvaluator(long seed) {
        super(seed);
    }

    @Override
	public ImmutableSet<DefaultParcel> evaluateState(long time) {
		ImmutableSet.Builder<DefaultParcel> ret = ImmutableSet.builder();
		Map<DefaultParcel, Double> slacks = calculateSlackForState(time);

		for (DefaultParcel par : slacks.keySet()) {
			if (slacks.get(par) <= 10000) {
				//System.out.println("Removing " + par + " with slack " + slacks.get(par));
				ret.add(par);
			}
		}

		return ret.build();
	}

	public static SupplierRng<? extends FixedSlackEvaluator> supplier() {
		return new SupplierRng.DefaultSupplierRng<FixedSlackEvaluator>() {
			@Override
			public FixedSlackEvaluator get(long seed) {
				return new FixedSlackEvaluator(seed);
			}
		};
	}
}
