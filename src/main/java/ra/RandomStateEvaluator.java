package ra;

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
	private long nextReEvaluation = 50;
	private Random rng;

	public RandomStateEvaluator(long seed) {
		rng = new Random(seed);
	}

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(ImmutableSet<DefaultParcel> state, long time) {
		if (state.isEmpty())
			return ImmutableSet.of();

		int nb;

		if ((nb = rng.nextInt(10 * state.size())) < state.size()) {
			return ImmutableSet.of(state.asList().get(nb));
		}

		return ImmutableSet.of();
	}

	@Override
	public boolean shouldReEvaluate(long ticks) {
		if (ticks >= nextReEvaluation) {
			Random rng = new Random();

			nextReEvaluation += rng.nextInt(50);

			return true;
		}

		return false;
	}

	public static SupplierRng<? extends StateEvaluator> supplier() {
		return new SupplierRng.DefaultSupplierRng<RandomStateEvaluator>() {

			@Override
			public RandomStateEvaluator get(long seed) {
				return new RandomStateEvaluator(seed);
			}
		};
	}
}
