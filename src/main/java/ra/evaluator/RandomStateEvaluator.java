package ra.evaluator;

import com.google.common.collect.ImmutableSet;
import common.truck.StateEvaluator;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Random;

/**
 * Randomly removes a parcel from the truck, every 50+/-25 ticks with a chance of 10% every time it's called.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class RandomStateEvaluator extends StateEvaluator {
    private final int percentage;
    private long nextReEvaluation = 50;
	private Random rng;

    /**
     *
     * @param seed Seed for the internal RNG
     * @param percentage Percentage chance every parcel has to be re-auctioned (0-100).
     */
	public RandomStateEvaluator(long seed, int percentage) {
        this.percentage = percentage;
        rng = new Random(seed);
	}

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(long time) {
		if (getTruck().getParcels().isEmpty())
			return ImmutableSet.of();

		int nb;

		if ((nb = rng.nextInt(100 / percentage * getTruck().getParcels().size())) < getTruck().getParcels().size()) {
			return ImmutableSet.of(getTruck().getParcels().asList().get(nb));
		}

		return ImmutableSet.of();
	}

	@Override
	public boolean shouldReEvaluate(long ticks) {
		if (ticks >= nextReEvaluation) {
			nextReEvaluation += rng.nextInt(50);

			return true;
		}

		return false;
	}

	public static SupplierRng<? extends StateEvaluator> supplier(final int percentage) {
		return new SupplierRng.DefaultSupplierRng<RandomStateEvaluator>() {
			@Override
			public RandomStateEvaluator get(long seed) {
				return new RandomStateEvaluator(seed, percentage);
			}

            @Override
            public String toString() {
                return "RandomStateEvaluator" + percentage;
            }
        };
	}
}
