package ra;

import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.common.StatisticsDTO;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Jacobs14ObjectiveFunction implements ObjectiveFunction {
    protected final double alpha;
    protected final double beta;

    public Jacobs14ObjectiveFunction() {
        alpha = 1d;
        beta = 1d;
    }

    /**
     * All parcels need to be delivered, all vehicles need to be back at the
     * depot.
     */
    @Override
    public boolean isValidResult(StatisticsDTO stats) {
        return stats.totalParcels == stats.acceptedParcels
                && stats.totalParcels == stats.totalPickups
                && stats.totalParcels == stats.totalDeliveries && stats.simFinish
                && stats.totalVehicles == stats.vehiclesAtDepot;
    }

    /**
     * Computes the cost according to the definition of the paper: <i>the cost
     * function used throughout this work is to minimize a weighted sum of three
     * different criteria: total travel time, sum of lateness over all pick-up and
     * delivery locations and sum of overtime over all vehicles</i>. The function
     * is defined as:
     * <code>sum(Tk) + alpha sum(max(0,tv-lv)) + beta sum(max(0,tk-l0))</code>
     * Where: Tk is the total travel time on route Rk, alpha and beta are
     * weighting parameters which were set to 1 in the paper. The definition of
     * lateness: <code>max(0,lateness)</code> is commonly referred to as
     * <i>tardiness</i>. All times are expressed in minutes.
     *
     */
    @Override
    public double computeCost(StatisticsDTO stats) {
        final double totalTravelTime = travelTime(stats);
        final double sumTardiness = tardiness(stats);
        final double overTime = overTime(stats);
        return totalTravelTime + (alpha * sumTardiness) + (beta * overTime);
    }

    @Override
    public String printHumanReadableFormat(StatisticsDTO stats) {
        return new StringBuilder().append("Travel time: ")
                .append(travelTime(stats)).append("\nTardiness: ")
                .append(tardiness(stats)).append("\nOvertime: ")
                .append(overTime(stats)).append("\nTotal: ").append(computeCost(stats))
                .toString();

    }

    // time in minutes
    public double travelTime(StatisticsDTO stats) {
        // avg speed is 30 km/h
        // = (dist / 30.0) * 60.0
        return stats.totalDistance * 2d;
    }

    public double tardiness(StatisticsDTO stats) {
        return (stats.pickupTardiness + stats.deliveryTardiness) / 60000d;
    }

    public double overTime(StatisticsDTO stats) {
        return stats.overTime / 60000d;
    }
}
