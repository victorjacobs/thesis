package ra.evaluator.heuristic;

import com.google.common.collect.ImmutableMap;
import common.truck.Truck;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.Map;
import java.util.Random;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class RandomHeuristic implements ReAuctionHeuristic {
    private final Random rng;

    public RandomHeuristic() {
        rng = new Random();
    }

    @Override
    public Map<DefaultParcel, Double> evaluate(Truck truck, long time) {
        ImmutableMap.Builder<DefaultParcel, Double> ret = ImmutableMap.builder();

        for (DefaultParcel par : truck.getParcels()) {
            ret.put(par, (double) rng.nextInt(10000));
        }

        return ret.build();
    }
}
