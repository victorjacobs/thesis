package common.truck;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import common.ReAuctionableParcel;
import common.truck.route.RoutePlanner;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.sim.core.Simulator;
import rinde.sim.core.model.Model;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.AddVehicleEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem.Creator;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.pdptw.experiment.DefaultMASConfiguration;
import rinde.sim.util.SupplierRng;

/**
 * A {@link rinde.sim.pdptw.experiment.MASConfiguration} that configures a
 * simulation to use a {@link Truck} instance as vehicle.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class TruckConfiguration extends DefaultMASConfiguration {
	/**
	 * Supplier for {@link RoutePlanner} instances, it supplies a new instance for
	 * <i>every</i> {@link Truck}.
	 */
	protected final SupplierRng<? extends RoutePlanner> rpSupplier;

	/**
	 * Supplier for {@link Communicator} instances, it supplies a new instance for
	 * <i>every</i> {@link Truck}.
	 */
	protected final SupplierRng<? extends Bidder> bSupplier;

	/**
	 * Suppliers for {@link Model}s, for each model a new instance is created for
	 * each simulation.
	 */
	protected final ImmutableList<? extends SupplierRng<? extends Model<?>>> mSuppliers;
	private /*final*/ ImmutableList<? extends SupplierRng<? extends StateEvaluator>> seSuppliers;
	private /*final*/ ImmutableList<? extends SupplierRng<? extends StateObserver>> soSuppliers;

	/**
	 * Instantiate a new configuration.
	 * @param routePlannerSupplier {@link #rpSupplier}.
	 * @param bidderSupplier {@link #bSupplier}.
	 * @param modelSuppliers {@link #mSuppliers}.
	 */
	public TruckConfiguration(
			SupplierRng<? extends RoutePlanner> routePlannerSupplier,
			SupplierRng<? extends Bidder> bidderSupplier,
			ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers/*,
			ImmutableList<? extends SupplierRng<? extends StateObserver>> stateObserverSuppliers,
			ImmutableList<? extends SupplierRng<? extends StateEvaluator>> stateEvaluatorSuppliers*/) {
		rpSupplier = routePlannerSupplier;
		bSupplier = bidderSupplier;
		mSuppliers = modelSuppliers;
		/*soSuppliers = stateObserverSuppliers;
		seSuppliers = stateEvaluatorSuppliers;*/
	}

	@Override
	public Creator<AddVehicleEvent> getVehicleCreator() {
		return new Creator<AddVehicleEvent>() {
			@Override
			public boolean create(Simulator sim, AddVehicleEvent event) {
				final RoutePlanner rp = rpSupplier.get(sim.getRandomGenerator()
						.nextLong());
				final Bidder b = bSupplier.get(sim.getRandomGenerator()
						.nextLong());
				return sim.register(createTruck(event.vehicleDTO, rp, b, sim));
			}
		};
	}

	@Override
	public Optional<? extends Creator<AddParcelEvent>> getParcelCreator() {
		return Optional.of(ReAuctionableParcel.getCreator());
	}

	/**
	 * Factory method that can be overridden by subclasses that want to use their
	 * own {@link Truck} implementation.
	 *
	 * @param dto The {@link rinde.sim.pdptw.common.VehicleDTO} containing the vehicle information.
	 * @param rp The {@link common.truck.route.RoutePlanner} to use in the truck.
	 * @param b The {@link rinde.logistics.pdptw.mas.comm.Communicator} to use in the truck.
	 * @param sim
	 * @return The newly created truck.
	 */
	protected Truck createTruck(VehicleDTO dto, RoutePlanner rp, Bidder b, Simulator sim) {
		Truck ret = new Truck(dto, rp, b);

//		// Bind observers
//		for (SupplierRng<? extends StateObserver> so : soSuppliers) {
//			ret.addStateObserver(so.get(sim.getRandomGenerator().nextLong()));
//		}
//
//		// Bind evaluators
//		for (SupplierRng<? extends StateEvaluator> so : seSuppliers) {
//			ret.addStateEvaluator(so.get(sim.getRandomGenerator().nextLong()));
//		}

		return ret;
	}

	@Override
	public ImmutableList<? extends SupplierRng<? extends Model<?>>> getModels() {
		return mSuppliers;
	}

	@Override
	public String toString() {
		return Joiner.on("-").join(rpSupplier, bSupplier, mSuppliers.toArray());
	}
}
