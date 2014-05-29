package ra.evaluator;

import common.truck.StateEvaluator;
import ra.evaluator.heuristic.NegativePriorityHeuristic;
import ra.evaluator.heuristic.ReAuctionHeuristic;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.Map;
import java.util.Random;

import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * Base class for all {@link StateEvaluator}s that use the slack measure as heuristic for re-auctioning.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class HeuristicEvaluator extends StateEvaluator {
    protected Random rng;
    private long nextReEvaluation = 50;
    private final ReAuctionHeuristic heuristic;

    public HeuristicEvaluator(ReAuctionHeuristic h, long seed) {
        this.heuristic = h;
        this.rng = new Random(seed);
    }

    public HeuristicEvaluator(long seed) {
        this(new NegativePriorityHeuristic(), seed);
    }

    /**
     * This method calculates the slack for every parcel owned by the truck. With "slack" is meant "the maximum
     * amount of time the parcel can be delayed in the current schedule for it to be still on time". If effectively
     * gives a measure for the flexibility of a certain parcel.
     *
     * @param time Current simulation time
     * @return Map containing the slack for every parcel in the truck
     */
    Map<DefaultParcel, Double> calculateSlackForState(long time) {
        return heuristic.evaluate(getTruck(), time);
    }

    @Override
    public boolean shouldReEvaluate(long ticks) {
        // TODO placeholder
        if (ticks >= nextReEvaluation) {
            nextReEvaluation += rng.nextInt(50);

            return true;
        }

        return false;
    }
}
