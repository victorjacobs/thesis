package ra.evaluator;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Collection;
import java.util.Map;

/**
 * This is exactly the same as {@link ra.evaluator.AdaptiveSlackEvaluator}, except that it uses Apache math commons
 * library to compute mean and standard deviation. Mainly for comparative purposes.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class AdaptiveSlackEvaluatorApache extends SlackEvaluator {
    private final float numberStandardDeviations;
    private StandardDeviation standardDeviation;
    private Mean mean;

    public AdaptiveSlackEvaluatorApache(long seed) {
        this(seed, 1);
    }

	public AdaptiveSlackEvaluatorApache(long seed, float numberStandardDeviations) {
		super(seed);
        this.numberStandardDeviations = numberStandardDeviations;

        standardDeviation = new StandardDeviation();
        mean = new Mean();
	}

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(long time) {
		ImmutableSet.Builder<DefaultParcel> ret = new ImmutableSet.Builder<DefaultParcel>();
		Map<DefaultParcel, Double> slacks = calculateSlackForState(time);
		// Update local values
		update(slacks.values());

		double curSlack;
		for (DefaultParcel par : slacks.keySet()) {
			// Always add parcels with negative slacks
			if ((curSlack = slacks.get(par)) < 0) {
				ret.add(par);
			} else if (curSlack < getMean() - numberStandardDeviations * getStandardDeviation()) {
				ret.add(par);
			}
		}

		return ret.build();
	}

    private double getStandardDeviation() {
        return (Double.isNaN(standardDeviation.getResult())) ? 0 : standardDeviation.getResult();
    }

    private double getMean() {
        return (Double.isNaN(mean.getResult())) ? 0 : mean.getResult();
    }

    // On line standard variance calculation after Knuth, use this in favor of apache commons math library since this
	// uses less memory (only needs to keep 3 variables)
	void update(Collection<Double> slacks) {
		for (Double el : slacks) {
			mean.increment(el);
            standardDeviation.increment(el);
		}
	}

    public static SupplierRng<? extends AdaptiveSlackEvaluatorApache> supplier() {
        return supplier(1);
    }

    /**
     * Returns a supplier of the AdaptiveSlackEvaluator object, given a number of standard deviations which is the
     * threshold for auctioning (higher means less re-auctions).
     *
     * @param numberStandardDeviations
     * @return
     */
	public static SupplierRng<? extends AdaptiveSlackEvaluatorApache> supplier(final float numberStandardDeviations) {
		return new SupplierRng.DefaultSupplierRng<AdaptiveSlackEvaluatorApache>() {
			@Override
			public AdaptiveSlackEvaluatorApache get(long seed) {
				return new AdaptiveSlackEvaluatorApache(seed, numberStandardDeviations);
			}

            @Override
            public String toString() {
                return "AdaptiveSlackEvaluatorApache" + numberStandardDeviations + "STD";
            }
        };
	}
}
