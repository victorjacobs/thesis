package common.truck;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import common.auctioning.Auctioneer;
import common.baseline.SolverBidder;
import common.results.ParcelTrackerModel;
import common.truck.route.RoutePlanner;
import common.truck.route.SolverRoutePlanner;
import ra.evaluator.AgentParcelSlackEvaluator;
import ra.parcel.AdaptiveSlackReAuctionableParcel;
import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.core.Simulator;
import rinde.sim.core.model.Model;
import rinde.sim.pdptw.common.*;
import rinde.sim.pdptw.common.DynamicPDPTWProblem.Creator;
import rinde.sim.pdptw.experiment.DefaultMASConfiguration;
import rinde.sim.util.SupplierRng;

import static com.google.common.base.Preconditions.checkState;

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
    private final Creator<AddParcelEvent> parcelCreator;
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
			ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers,
			ImmutableList<? extends SupplierRng<? extends StateObserver>> stateObserverSuppliers,
			ImmutableList<? extends SupplierRng<? extends StateEvaluator>> stateEvaluatorSuppliers,
            DynamicPDPTWProblem.Creator<AddParcelEvent> parcelCreator) {
		rpSupplier = routePlannerSupplier;
		bSupplier = bidderSupplier;
		mSuppliers = modelSuppliers;
		soSuppliers = stateObserverSuppliers;
		seSuppliers = stateEvaluatorSuppliers;
        this.parcelCreator = parcelCreator;
    }

    public TruckConfiguration(
            SupplierRng<? extends RoutePlanner> routePlannerSupplier,
            SupplierRng<? extends Bidder> bidderSupplier,
            ImmutableList<? extends SupplierRng<? extends Model<?>>> modelSuppliers,
            ImmutableList<? extends SupplierRng<? extends StateEvaluator>> stateEvaluatorSuppliers,
            DynamicPDPTWProblem.Creator<AddParcelEvent> parcelCreator) {
        this(routePlannerSupplier, bidderSupplier, modelSuppliers, ImmutableList.<SupplierRng<? extends StateObserver>>of(), stateEvaluatorSuppliers,
                parcelCreator);
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

	/**
	 * Override to allow TruckConfiguration to set different parcel creators.
     *
	 * @return Parcel creator defined by this TruckConfiguration
	 */
	@Override
	public Optional<? extends Creator<AddParcelEvent>> getParcelCreator() {
		return Optional.of(parcelCreator);
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

		// Bind observers
		for (SupplierRng<? extends StateObserver> so : soSuppliers) {
            StateObserver observer = so.get(sim.getRandomGenerator().nextLong());
            observer.setTruck(ret);
			ret.addStateObserver(observer);
		}

		// Bind evaluators
		for (SupplierRng<? extends StateEvaluator> se : seSuppliers) {
			StateEvaluator s = se.get(sim.getRandomGenerator().nextLong());
			s.setTruck(ret);
			ret.addStateEvaluator(s);
		}

		return ret;
	}

	@Override
	public ImmutableList<? extends SupplierRng<? extends Model<?>>> getModels() {
		return mSuppliers;
	}

	@Override
	public String toString() {
        // TODO
        return Joiner.on("-").join(seSuppliers.get(0), parcelCreator);
	}

    /**
     * Builds a TruckConfiguration.
     */
    @SuppressWarnings("all")    // Some generics thing
    public static class Builder {
        private SupplierRng<? extends RoutePlanner> routePlannerSupplier;
        private SupplierRng<? extends Bidder> bidderSupplier;
        private ImmutableList.Builder<? extends SupplierRng<? extends Model<?>>> modelSuppliers;
        private ImmutableList.Builder<? extends SupplierRng<? extends StateObserver>> stateObserverSuppliers;
        private ImmutableList.Builder<? extends SupplierRng<? extends StateEvaluator>> stateEvaluatorSuppliers;
        private DynamicPDPTWProblem.Creator<AddParcelEvent> parcelCreator;

        public Builder() {
            modelSuppliers = ImmutableList.builder();
            stateObserverSuppliers = ImmutableList.builder();
            stateEvaluatorSuppliers = ImmutableList.builder();
        }

        public Builder withRoutePlanner(SupplierRng<? extends RoutePlanner> routePlannerSupplier) {
            this.routePlannerSupplier = routePlannerSupplier;
            return this;
        }

        public Builder withBidder(SupplierRng<? extends Bidder> bidderSupplier) {
            this.bidderSupplier = bidderSupplier;
            return this;
        }

        public Builder addModel(SupplierRng<? extends Model<?>> modelSupplier) {
            modelSuppliers.add(modelSupplier);
            return this;
        }

        public Builder addStateObserver(SupplierRng<? extends StateObserver> stateObserverSupplier) {
            stateObserverSuppliers.add(stateObserverSupplier);
            return this;
        }

        public Builder addStateEvaluator(SupplierRng<? extends StateEvaluator> stateEvaluatorSupplier) {
            stateEvaluatorSuppliers.add(stateEvaluatorSupplier);
            return this;
        }

        public Builder withParcelCreator(DynamicPDPTWProblem.Creator<AddParcelEvent> parcelCreator) {
            this.parcelCreator = parcelCreator;
            return this;
        }

        public TruckConfiguration build() {
            checkState(routePlannerSupplier != null, "TruckConfiguration needs a route planner");
            checkState(bidderSupplier != null, "TruckConfiguration needs bidder");
            checkState(parcelCreator != null, "TruckConfiguration needs parcel creator");

            return new TruckConfiguration(
                    routePlannerSupplier,
                    bidderSupplier,
                    modelSuppliers.build(),
                    stateObserverSuppliers.build(),
                    stateEvaluatorSuppliers.build(),
                    parcelCreator
            );
        }
    }
}
