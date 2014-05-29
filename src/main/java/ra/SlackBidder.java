package ra;

import common.baseline.SolverBidder;
import common.truck.Bid;
import ra.evaluator.heuristic.SlackHeuristic;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.util.SupplierRng;

import java.util.Map;

/**
 * Bids slack value for a parcel
 *
 * TODO this is broken since the parcel being bid on doesn't have any slack yet -> needs simulated truck or something
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class SlackBidder extends SolverBidder {
    public SlackBidder(long seed){
        super(new Gendreau06ObjectiveFunction(), MultiVehicleHeuristicSolver.supplier(50, 1000).get(seed));
    }

    @Override
    public Bid getBidFor(DefaultParcel par, long time) {
        SlackHeuristic heuristic = new SlackHeuristic();

        Map<DefaultParcel, Double> slacks = heuristic.evaluate(truck, time);

        // Optimise for maximum slack, ergo the bid should be negative slack (Auctioneer optimises to min)
        double bid = (slacks.containsKey(par)) ? -slacks.get(par) : super.getBidFor(par, time).getBidValue();

        return new Bid(this, par, bid);
    }

    public static SupplierRng<SlackBidder> supplier() {
        return new SupplierRng.DefaultSupplierRng<SlackBidder>() {
            @Override
            public SlackBidder get(long seed) {
                return new SlackBidder(seed);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        };
    }
}
