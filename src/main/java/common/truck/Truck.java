package common.truck;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import common.truck.route.RoutePlanner;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TimeLapse;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.road.RoadModel;
import rinde.sim.event.Event;
import rinde.sim.event.Listener;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.RouteFollowingVehicle;
import rinde.sim.pdptw.common.VehicleDTO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

/**
 *
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Truck extends RouteFollowingVehicle implements Listener, SimulatorUser {

	// State
	private Set<DefaultParcel> state;
	private DefaultParcel nextParcel;
	// Components
	private List<StateObserver> stateObservers;
	private List<StateEvaluator> stateEvaluators;
	private Bidder bidder;
	private RoutePlanner routePlanner;	// TODO RP is both set here and in the stateObservers -> no longer needed?
	// TODO actually need ticksSinceLastReEvaluation per evaluator, but for now assume only one
	private int ticksSinceLastReEvaluation = 0;
	// TODO needs getter to routeplanner
	// TODO binding for TickListener?

	/**
	 * Initializes the vehicle.
	 *
	 * @param pDto                      The {@link rinde.sim.pdptw.common.VehicleDTO} that defines this vehicle.
	 * @param allowDelayedRouteChanging This boolean changes the behavior of the
	 *                                  {@link #setRoute(java.util.Collection)} method.
	 */
	public Truck(VehicleDTO pDto, boolean allowDelayedRouteChanging) {
		super(pDto, allowDelayedRouteChanging);
		pdpModel = Optional.absent();

		stateObservers = newLinkedList();
		stateEvaluators = newLinkedList();
	}

	// Setup
	// TODO for now these are public since anything should be allowed to subscribe to the events (?)
	public void addStateObserver(StateObserver l) {
		stateObservers.add(l);
	}

	public void addStateEvaluator(StateEvaluator s) {
		stateEvaluators.add(s);
	}

	/**
	 * Doubly binds a bidder to the current truck. Result is that Truck has a reference to the bidder and vice versa.
	 * @param bidder Bidder to be bound to this Truck
	 */
	public void bindBidder(Bidder bidder) {
		checkState(bidder == null, "Bidder already bound to Truck");

		this.bidder = bidder;
		bidder.bindTruck(this);
	}

	/**
	 * Doubly binds a route planner to this truck. Result is that the truck has a reference to the route planner and
	 * that the route planner is subscribed to state change notifications from the Truck.
	 * @param routePlanner
	 */
	public void bindRoutePlanner(RoutePlanner routePlanner) {
		checkState(routePlanner == null, "Routeplanner already bound to Truck");

		routePlanner.bindTruck(this);
		this.routePlanner = routePlanner;	// TODO is this needed?
		addStateObserver(routePlanner);
	}

	// Manage state
	public void removeParcel(DefaultParcel par) {
		checkArgument(state.contains(par));
		state.remove(par);
		stateChanged();
	}

	public void addParcel(DefaultParcel par) {
		checkArgument(!state.contains(par));
		state.add(par);
		stateChanged();
	}

	private void stateChanged() {
		for (StateObserver l : stateObservers) {
			l.notifyStateChanged(ImmutableSet.copyOf(state), getCurrentTime().getTime());
		}
	}

	public ImmutableSet<DefaultParcel> getParcels() {
		return ImmutableSet.copyOf(state);
	}

	@Override
	public void setRoute(Collection<DefaultParcel> r) {
		super.setRoute(r);
		if (r.size() > 0)
			nextParcel = r.iterator().next();
	}

	@Override
	public void afterTick(TimeLapse time) {
		for (StateObserver o : stateObservers) {
			if (o.reEvaluateState(ticksSinceLastReEvaluation, getCurrentTime().getTime())) {
				ticksSinceLastReEvaluation = 0;	// Reset
			}
		}

		ticksSinceLastReEvaluation++;
	}

	@Override
	public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
		super.initRoadPDP(pRoadModel, pPdpModel);
		// Don't need to actually do anything here, the truck already has references to both the roadmodel as the
		// pdpmodel through PDPObjectImpl
	}

	@Override
	public void handleEvent(Event e) {
		// TODO do something on state change here (?)
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		// TODO What here?
	}

	// TODO these next two are private in PDPObjectImpl, is there any problem just exposing them?
	public PDPModel getPdpModel() {
		return this.pdpModel.get();
	}

	public RoadModel getRoadModel() {
		return getRoadModel();
	}
}
