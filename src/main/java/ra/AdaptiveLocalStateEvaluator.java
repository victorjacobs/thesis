package ra;

import com.google.common.collect.ImmutableSet;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Collection;
import java.util.Map;

/**
 * This variant of {@link ra.LocalStateEvaluator} adapts the threshold for re-auctioning. It keeps a running mean and
 * variance to determine whether a certain value is too high or too low.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class AdaptiveLocalStateEvaluator extends LocalStateEvaluator {
	private double threshold;

	private double mean;
	private int n;
	private int M2;
	private int variance;

	public AdaptiveLocalStateEvaluator(long seed) {
		super(seed);

		n = 0;
		mean = 0;
		M2 = 0;
		variance = 0;
	}

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(long time) {
		ImmutableSet.Builder<DefaultParcel> ret = new ImmutableSet.Builder<DefaultParcel>();
		Map<DefaultParcel, Double> slacks = calculateSlackForState();
		// Update local values
		update(slacks.values());

		double curSlack;
		for (DefaultParcel par : slacks.keySet()) {
			// Always add parcels with negative slacks
			if ((curSlack = slacks.get(par)) < 0) {
				ret.add(par);
			} else if (curSlack < mean - getStandardDeviation()) {
				ret.add(par);
			}
		}

		return ret.build();
	}

	// On line standard variance calculation after Knuth
	private void update(Collection<Double> slacks) {
		double delta;

		for (Double el : slacks) {
			n++;
			delta = el - mean;
			mean += delta / n;
			M2 += delta * (el - mean);
		}

		if (n > 1)
			variance = M2 / (n - 1);
	}

	private Double getStandardDeviation() {
		return Math.sqrt(variance);
	}

	public static SupplierRng<? extends AdaptiveLocalStateEvaluator> supplier() {
		return new SupplierRng.DefaultSupplierRng<AdaptiveLocalStateEvaluator>() {
			@Override
			public AdaptiveLocalStateEvaluator get(long seed) {
				return new AdaptiveLocalStateEvaluator(seed);
			}
		};
	}
}
