package ra.evaluator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.truck.StateEvaluator;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Random;

/**
 * Identical to {@link ra.evaluator.RandomStateEvaluator}, but every {@link #evaluateState(long)} can return more parcels instead of
 * just one
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class RandomStateEvaluatorMultipleParcels extends StateEvaluator {
    private final int percentage;
    private long nextReEvaluation = 50;
	private Random rng;

    /**
     *
     * @param seed Seed for the internal RNG
     * @param percentage Percentage chance every parcel has to be re-auctioned (0-100).
     */
	public RandomStateEvaluatorMultipleParcels(long seed, int percentage) {
        this.percentage = percentage;
        rng = new Random(seed);
	}

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(long time) {
		if (getTruck().getParcels().isEmpty())
			return ImmutableSet.of();

        ImmutableSet.Builder<DefaultParcel> builder = new ImmutableSet.Builder<DefaultParcel>();

		for (DefaultParcel par : getTruck().getParcels()) {
            if (rng.nextInt(100) < percentage)
                builder.add(par);
        }

		return builder.build();
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
		return new SupplierRng.DefaultSupplierRng<RandomStateEvaluatorMultipleParcels>() {
			@Override
			public RandomStateEvaluatorMultipleParcels get(long seed) {
				return new RandomStateEvaluatorMultipleParcels(seed, percentage);
			}

            @Override
            public String toString() {
                return "RandomStateEvaluatorMultipleParcels" + percentage;
            }
        };
	}
}