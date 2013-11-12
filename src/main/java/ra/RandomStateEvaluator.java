package ra;

import com.google.common.collect.ImmutableSet;
import common.truck.StateEvaluator;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 05/11/13
 * Time: 17:01
 */
public class RandomStateEvaluator extends StateEvaluator {

	private long nextReEvaluation = 50;

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(ImmutableSet<DefaultParcel> state, long time) {
		if (state.isEmpty())
			return null;

		Random rng = new Random();
		int nb;

		if ((nb = rng.nextInt(10 * state.size())) < state.size()) {
			return ImmutableSet.of(state.asList().get(nb));
		}

		return null;
	}

	@Override
	public boolean shouldReEvaluate(long ticks) {
		if (ticks == nextReEvaluation) {
			Random rng = new Random();

			nextReEvaluation += rng.nextInt(50);

			return true;
		}

		return false;
	}
}
