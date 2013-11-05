/**
 *
 */
package common.truck.route;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.truck.Truck;
import common.truck.StateObserver;
import rinde.logistics.pdptw.mas.route.AbstractRoutePlanner;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.pdptw.central.GlobalStateObject;
import rinde.sim.pdptw.central.Solver;
import rinde.sim.pdptw.central.SolverValidator;
import rinde.sim.pdptw.central.Solvers;
import rinde.sim.pdptw.central.Solvers.SimulationSolver;
import rinde.sim.pdptw.central.Solvers.SolveArgs;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.PDPRoadModel;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * A {@link rinde.logistics.pdptw.mas.route.RoutePlanner} implementation that uses a {@link Solver} that
 * computes a complete route each time {@link #update(Collection, long)} is
 * called.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 */
public class SolverRoutePlanner extends AbstractRoutePlanner implements
		SimulatorUser, StateObserver {

	private final Solver solver;
	private Truck truck;
	private Queue<? extends DefaultParcel> route;
	private Optional<SimulationSolver> solverHandle;
	private Optional<SimulatorAPI> simulator;

	/**
	 * Create a route planner that uses the specified {@link Solver} to compute
	 * the best route.
	 * @param s {@link Solver} used for route planning.
	 */
	public SolverRoutePlanner(Solver s, Truck truck) {
		solver = s;
		this.truck = truck;
		route = newLinkedList();
		solverHandle = Optional.absent();
		simulator = Optional.absent();
	}

	@Override
	public void notifyStateChanged(ImmutableSet<DefaultParcel> newState, long time) {
		// For now just re-initialize the entire Route Planner to make sure no state is left over in the solver
		// TODO this is a LOT of computation that's done every state change.
		solverHandle = Optional.absent();
		route = newLinkedList();
		initSolver();

		doUpdate(newState, time);
		truck.setRoute(new LinkedList<DefaultParcel>(route));
	}

	@Override
	public boolean reEvaluateState(int ticksSinceLastCall, long time) {
		return false;
	}

	/**
	 * Calling this method overrides the route of this planner. This method has
	 * similar effect as {@link #update(Collection, long)} except that no
	 * computations are done.
	 * @param r The new route.
	 */
	public void changeRoute(Queue<? extends DefaultParcel> r) {
		updated = true;
		route = newLinkedList(r);
	}

	@Override
	protected void doUpdate(Collection<DefaultParcel> onMap, long time) {
		if (onMap.isEmpty() && pdpModel.get().getContents(vehicle.get()).isEmpty()) {
			route.clear();
		} else {
			final SolveArgs args = SolveArgs.create().useParcels(onMap)
					.useCurrentRoutes(ImmutableList.of(ImmutableList.copyOf(route)));
			try {
				final GlobalStateObject gso = solverHandle.get().convert(args).state;
				SolverValidator.checkRoute(gso.vehicles.get(0), 0);
			} catch (final IllegalArgumentException e) {
				args.noCurrentRoutes();
			}
			route = solverHandle.get().solve(args).get(0);
		}
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		simulator = Optional.of(api);
		initSolver();
	}

	private void initSolver() {
		if (!solverHandle.isPresent() && isInitialized() && simulator.isPresent()) {
			solverHandle = Optional.of(Solvers.solverBuilder(solver)
					.with((PDPRoadModel) roadModel.get()).with(pdpModel.get())
					.with(simulator.get()).with(vehicle.get()).buildSingle());
		}
	}

	@Override
	protected void afterInit() {
		initSolver();
	}

	@Override
	public boolean hasNext() {
		return !route.isEmpty();
	}

	@Override
	public Optional<DefaultParcel> current() {
		return Optional.fromNullable((DefaultParcel) route.peek());
	}

	@Override
	public Optional<ImmutableList<DefaultParcel>> currentRoute() {
		if (route.isEmpty()) {
			return Optional.absent();
		}
		return Optional.of(ImmutableList.copyOf(route));
	}

	@Override
	protected void nextImpl(long time) {
		route.poll();
	}
}