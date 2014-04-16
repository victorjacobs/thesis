package ra.evaluator;

import common.truck.StateEvaluator;
import ra.evaluator.heuristic.ReAuctionHeuristic;
import ra.evaluator.heuristic.SlackHeuristic;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * Base class for all {@link StateEvaluator}s that use the slack measure as heuristic for re-auctioning.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class SlackEvaluator extends StateEvaluator {
    protected Random rng;
    private long nextReEvaluation = 50;
    private final ReAuctionHeuristic heuristic;

    public SlackEvaluator(ReAuctionHeuristic h, long seed) {
        this.heuristic = h;
        this.rng = new Random(seed);
    }

    public SlackEvaluator(long seed) {
        this(new SlackHeuristic(), seed);
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
