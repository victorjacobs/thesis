package ra.evaluator.heuristic;

import common.truck.Truck;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Stub implements ReAuctionHeuristic {
    @Override
    public Map<DefaultParcel, Double> evaluate(Truck truck, long time) {
        Map<DefaultParcel, Double> ret = new HashMap<DefaultParcel, Double>();

        for (DefaultParcel par : truck.getParcels()) {
            //ret.put(par, par.)
        }

        return ret;
    }
}
