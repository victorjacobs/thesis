package ra.evaluator.heuristic;

import common.truck.Truck;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ExponentialNegativeHeuristic implements ReAuctionHeuristic {
    @Override
    public Map<DefaultParcel, Double> evaluate(Truck truck, long time) {
        double curTime = time;

        Map<DefaultParcel, Double> slacks = new HashMap<DefaultParcel, Double>();
        Set<Parcel> simulatedCargo = newLinkedHashSet(truck.getContents());
        Point simulatedPosition = truck.getPosition();

        for (DefaultParcel par : truck.getRoute()) {
            if (simulatedCargo.contains(par)) {
                // Delivering
                curTime += getTravelTimeBetween(truck, simulatedPosition, par.getDestination());

                // If arrive before timewindow, truck has to wait
                curTime = (curTime < par.getDeliveryTimeWindow().begin) ? par.getDeliveryTimeWindow().begin : curTime;

                // Don't bother adding slacks for parcels that are already in cargo (they are fixed)
                if (!truck.getContents().contains(par)) {
                    if (!slacks.containsKey(par)) {
                        slacks.put(par, par.getDeliveryTimeWindow().end - curTime);
                    } else {
                        double pickupSlack = slacks.get(par);
                        double deliverySlack = exponentify(par.getDeliveryTimeWindow().end - curTime);
                        double newValue;

                        if (pickupSlack < 0 && deliverySlack < 0)
                            newValue = pickupSlack + deliverySlack;
                        else if (pickupSlack < 0)
                            newValue = pickupSlack;
                        else if (deliverySlack < 0)
                            newValue = deliverySlack;
                        else
                            newValue = pickupSlack + deliverySlack;

                        slacks.put(par, newValue);
                    }
                }

                curTime += par.getDeliveryDuration();
                simulatedCargo.remove(par);
                simulatedPosition = par.getDestination();
            } else {
                // Picking up
                curTime += getTravelTimeBetween(truck, simulatedPosition, par.getPickupLocation());

                curTime = (curTime < par.getPickupTimeWindow().begin) ? par.getPickupTimeWindow().begin : curTime;

                // Don't bother adding slacks for parcels that are already in cargo (they are fixed)
                if (!truck.getContents().contains(par)) {
                    slacks.put(par, exponentify(par.getPickupTimeWindow().end - curTime));
                }

                curTime += par.getPickupDuration();
                simulatedCargo.add(par);
                simulatedPosition = par.getPickupLocation();
            }
        }

        return slacks;
    }

    private double getTravelTimeBetween(Truck truck, Point orig, Point dest) {
        // TODO assume straight paths
        double dist = Math.sqrt(Math.pow(orig.x - dest.x, 2) + Math.pow(orig.y - dest.y, 2));

        return dist / truck.getSpeed();
    }

    private double exponentify(double in) {
        return (Math.signum(in) < 0) ? in * in * in : in;
    }

    @Override
    public String toString() {
        return "ExponentialNegativeHeuristic";
    }
}
