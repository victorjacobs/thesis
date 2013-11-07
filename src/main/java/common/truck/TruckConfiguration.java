package common.truck;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import common.truck.route.RoutePlanner;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.sim.core.Simulator;
import rinde.sim.core.model.Model;
import rinde.sim.pdptw.common.AddVehicleEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem.Creator;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.pdptw.experiment.DefaultMASConfiguration;
import rinde.sim.util.SupplierRng;

/**
 * A {@link rinde.sim.pdptw.experiment.MASConfiguration} that configures a
 * simulation to use a {@link Truck} instance as vehicle.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>, Victor Jacobs <victor.jacobs@me.com>
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

	/**
	 * Instantiate a new configuration.
	 * @param routePlannerSupplier {@link #rpSupplier}.
	 * @param bidderSupplier {@link #bSupplier}.
	 * @param modelSuppliers {@link #mSuppliers}.
	 */
	public TruckConfiguration(
			SupplierRng<? extends RoutePlanner> routePlannerSupplier,
			SupplierRng<? extends Bidder> bidderSupplier,
			ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers) {
		rpSupplier = routePlannerSupplier;
		bSupplier = bidderSupplier;
		mSuppliers = modelSuppliers;
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
				return sim.register(createTruck(event.vehicleDTO, rp, b));
			}
		};
	}

	/**
	 * Factory method that can be overridden by subclasses that want to use their
	 * own {@link Truck} implementation.
	 * @param dto The {@link VehicleDTO} containing the vehicle information.
	 * @param rp The {@link RoutePlanner} to use in the truck.
	 * @param b The {@link Communicator} to use in the truck.
	 * @return The newly created truck.
	 */
	protected Truck createTruck(VehicleDTO dto, RoutePlanner rp, Bidder b) {
		return new Truck(dto, rp, b);
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
