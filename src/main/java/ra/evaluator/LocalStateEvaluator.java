package ra.evaluator;

import com.google.common.collect.ImmutableSet;
import common.truck.StateEvaluator;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * This state evaluator goes over all the parcels in the state and computes the slack for every one.
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class LocalStateEvaluator extends StateEvaluator {

	private long nextReEvaluation = 50;
	private Random rng;

	public LocalStateEvaluator(long seed) {
		this.rng = new Random(seed);
	}

	@Override
	public ImmutableSet<DefaultParcel> evaluateState(long time) {
		ImmutableSet.Builder<DefaultParcel> ret = ImmutableSet.builder();
		Map<DefaultParcel, Double> slacks = calculateSlackForState();

		for (DefaultParcel par : slacks.keySet()) {
			if (slacks.get(par) <= 10000) {
				//System.out.println("Removing " + par + " with slack " + slacks.get(par));
				ret.add(par);
			}
		}

		return ret.build();
	}

    /**
     * This method calculates the slack for every parcel owned by the truck.
     * TODO aanvullen
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

	public static SupplierRng<? extends LocalStateEvaluator> supplier() {
		return new SupplierRng.DefaultSupplierRng<LocalStateEvaluator>() {
			@Override
			public LocalStateEvaluator get(long seed) {
				return new LocalStateEvaluator(seed);
			}
		};
	}
}