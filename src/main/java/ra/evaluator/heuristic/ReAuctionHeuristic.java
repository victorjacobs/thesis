package ra.evaluator.heuristic;

import common.truck.Truck;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.Map;

/**
 * Represents a heuristic that measures the urgentness of delivery a certain parcel.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface ReAuctionHeuristic {

    public Map<DefaultParcel, Double> evaluate(Truck truck, long time);

}
