package ra;

import com.google.common.collect.ImmutableSet;
import common.truck.StateEvaluator;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * This state evaluator goes over all the parcels in the state and computes the slack for every one.
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class LocalStateEvaluator extends StateEvaluator {

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(long time) {
		ImmutableSet.Builder<DefaultParcel> ret = ImmutableSet.builder();
		Map<Parcel, Double> slacks = calculateSlackForState();

		for (DefaultParcel par : getTruck().getParcels()) {
			if (slacks.get(par) > 10) {
				ret.add(par);
			}
		}

		return ret.build();
	}

	private Map<Parcel, Double> calculateSlackForState() {
		double curTime = 0;

		Map<Parcel, Double> slacks = new HashMap<>();
		Set<Parcel> simulatedCargo = newLinkedHashSet(getTruck().getPdpModel().getContents(getTruck()));

		for (DefaultParcel par : getTruck().getRoute()) {
			if (simulatedCargo.contains(par)) {
				// Delivering
				curTime += getTravelTimeTo(par.getDestination());

				curTime += par.getDeliveryDuration();
				slacks.put(par, par.getDeliveryTimeWindow().end - curTime);
				simulatedCargo.remove(par);
			} else {
				// Picking up
				curTime += getTravelTimeTo(par.getPickupLocation());

				curTime += par.getPickupDuration();
				simulatedCargo.add(par);
			}
		}


		return slacks;
	}

	private double getTravelTimeTo(Point dest) {
		// TODO assume straight paths
		Point thisPos = getTruck().getRoadModel().getPosition(getTruck());

		double dist = Math.sqrt(Math.pow(thisPos.x - dest.x, 2) + Math.pow(thisPos.y - dest.y, 2));

		return dist / getTruck().getSpeed();
	}

	@Override
	public boolean shouldReEvaluate(long ticks) {
		return false;
	}
}
