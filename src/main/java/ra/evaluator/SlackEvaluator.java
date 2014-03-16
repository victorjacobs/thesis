package ra.evaluator;

import common.truck.StateEvaluator;
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

    public SlackEvaluator(long seed) {
        this.rng = new Random(seed);
    }

    /**
     * This method calculates the slack for every parcel owned by the truck. With "slack" is meant "the maximum
     * amount of time the parcel can be delayed in the current schedule for it to be still on time". If effectively
     * gives a measure for the flexibility of a certain parcel.
     *
     * @return Map containing the slack for every parcel in the truck
     */
    Map<DefaultParcel, Double> calculateSlackForState() {
        double curTime = 0;

        Map<DefaultParcel, Double> slacks = new HashMap<DefaultParcel, Double>();
        Set<Parcel> simulatedCargo = newLinkedHashSet(getTruck().getContents());
        Point simulatedPosition = getTruck().getPosition();

        for (DefaultParcel par : getTruck().getRoute()) {
            if (simulatedCargo.contains(par)) {
                // Delivering
                curTime += getTravelTimeBetween(simulatedPosition, par.getDestination());

                // If arrive before timewindow, truck has to wait
                curTime = (curTime < par.getDeliveryTimeWindow().begin) ? par.getDeliveryTimeWindow().begin : curTime;

                // Don't bother adding slacks for parcels that are already in cargo
                if (!getTruck().getContents().contains(par))
                    slacks.put(par, par.getDeliveryTimeWindow().end - curTime);

                curTime += par.getDeliveryDuration();
                simulatedCargo.remove(par);
                simulatedPosition = par.getDestination();
            } else {
                // Picking up
                curTime += getTravelTimeBetween(simulatedPosition, par.getPickupLocation());

                curTime = (curTime < par.getPickupTimeWindow().begin) ? par.getPickupTimeWindow().begin : curTime;

                curTime += par.getPickupDuration();
                simulatedCargo.add(par);
                simulatedPosition = par.getPickupLocation();
            }
        }

        return slacks;
    }

    private double getTravelTimeBetween(Point orig, Point dest) {
        // TODO assume straight paths
        double dist = Math.sqrt(Math.pow(orig.x - dest.x, 2) + Math.pow(orig.y - dest.y, 2));

        return dist / getTruck().getSpeed();
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
