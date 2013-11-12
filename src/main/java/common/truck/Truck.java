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
import rinde.sim.util.fsm.StateMachine;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 * Extends {@link rinde.sim.pdptw.common.RouteFollowingVehicle}, controlled by a {@link RoutePlanner} and {@link
 * Bidder}. The object itself contains all the state, namely all the parcels that will be handled by this Truck.
 * Every time this state changes, the {@link RoutePlanner} is notified to update the route. State changes happen
 * through the {@link Bidder} and a set of {@link StateEvaluator}s. The former makes decisions on what parcels to
 * handle in negotiation with other Trucks through the {@link common.Auctioneer} while the latter can make
 * independent decisions based on just the state.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Truck extends RouteFollowingVehicle implements Listener, SimulatorUser {

	// State TODO rename variable to something less ambiguous
	private Set<DefaultParcel> state;            // All parcels that will be/are(!) being handled by this truck
	// TODO maybe revert fixedParcels to single (since parcels that are picked up are already removed from state)
	private Set<DefaultParcel> fixedParcels;    // Parcels (plural!) that shouldn't be removed from the state
	// Components
	private List<StateObserver> stateObservers;
	private List<StateEvaluator> stateEvaluators;
	private Bidder bidder;
	private RoutePlanner routePlanner;    // TODO RP is both set here and in the stateObservers -> no longer needed?
	private long ticks = 0;
	// TODO needs getter to routeplanner
	// TODO add

	/**
	 * Initializes the vehicle.
	 *
	 * @param pDto The {@link rinde.sim.pdptw.common.VehicleDTO} that defines this vehicle.
	 * @param rp
	 * @param b
	 */
	public Truck(VehicleDTO pDto, RoutePlanner rp, Bidder b) {
		super(pDto, false);        // TODO no idea what this flag does
		pdpModel = Optional.absent();

		state = newLinkedHashSet();
		fixedParcels = newLinkedHashSet();
		stateObservers = newLinkedList();
		stateEvaluators = newLinkedList();

		bindBidder(b);
		bindRoutePlanner(rp);

		stateMachine.getEventAPI().addListener(this,
				StateMachine.StateMachineEvent.STATE_TRANSITION);
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
	 *
	 * @param bidder Bidder to be bound to this Truck
	 */
	private void bindBidder(Bidder bidder) {
		checkState(bidder != null, "Bidder already bound to Truck");

		this.bidder = bidder;
		bidder.bindTruck(this);
	}

	/**
	 * Doubly binds a route planner to this truck. Result is that the truck has a reference to the route planner and
	 * that the route planner is subscribed to state change notifications from the Truck.
	 *
	 * @param routePlanner
	 */
	private void bindRoutePlanner(RoutePlanner routePlanner) {
		checkState(routePlanner != null, "Route planner already bound to Truck");

		routePlanner.bindTruck(this);
		this.routePlanner = routePlanner;    // TODO is this needed?
		addStateObserver(routePlanner);
	}

	// Manage state
	public void removeParcel(DefaultParcel par) {
		checkArgument(state.contains(par));
		state.remove(par);
		fixedParcels.remove(par);
		stateChanged();
	}

	public void addParcel(DefaultParcel par) {
		checkArgument(!state.contains(par));
		state.add(par);
		stateChanged();
	}

	private void stateChanged() {
		// TODO give listeners reason why state changed?
		// For now not *really* needed (only route planner is listener) but might increase powerfulness of listeners

		// Since contents of this truck are added in the route planner, temporarily remove them here
		Set<DefaultParcel> newState = new HashSet<DefaultParcel>(state);
		newState.removeAll(pdpModel.get().getContents(this));

		for (StateObserver l : stateObservers) {
			l.notifyStateChanged(ImmutableSet.copyOf(newState), getCurrentTime().getTime());
		}
	}

	public ImmutableSet<DefaultParcel> getParcels() {
		return ImmutableSet.copyOf(state);
	}

	@Override
	public void afterTick(TimeLapse time) {
		for (StateEvaluator ev : stateEvaluators) {
			if (ev.shouldReEvaluate(ticks)) {
				ev.evaluateState(getCurrentTime().getTime());
			}
		}

		ticks++;
	}

	@Override
	public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
		super.initRoadPDP(pRoadModel, pPdpModel);
		// Don't need to actually do anything here, the truck already has references to both the roadmodel as the
		// pdpmodel through PDPObjectImpl
		routePlanner.init(pRoadModel, pPdpModel, this);
	}

	@Override
	public void handleEvent(Event e) {
		try {
			StateMachine.StateTransitionEvent<StateEvent, RouteFollowingVehicle> event =
					(StateMachine.StateTransitionEvent<StateEvent, RouteFollowingVehicle>) e;

			if (event.event == StateEvent.GOTO) {
				DefaultParcel cur = getRoute().iterator().next();

				// RouteFollowingVehicle decided to go to certain parcel

				// If the next stop on route is not yet in cargo (i.e. not underway to delivery location),
				// add it to list of parcels that are fixed in the state
				if (pdpModel.get().getParcelState(cur) != PDPModel.ParcelState.IN_CARGO) {
					fixedParcels.add(cur);
				}
			} else if (event.event == StateEvent.DONE) {
				// RouteFollowingVehicle is done servicing location, this might be when parcel is delivered or picked
				// up. In both cases, inform route planner

				Iterator<DefaultParcel> it = state.iterator();

				while (it.hasNext()) {
					if (pdpModel.get().getParcelState(it.next()) == PDPModel.ParcelState.DELIVERED)
						it.remove();
				}

				// Inform routeplanner to go to next goal
				stateChanged();
				routePlanner.next(getCurrentTime().getTime());
			}
		} catch (ClassCastException ex) {
			// Just continue, this wasn't an event that concerns us
		}
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		api.register(bidder);
		api.register(routePlanner);
	}

	// TODO these next two are private in PDPObjectImpl, is there any problem just exposing them?
	public PDPModel getPdpModel() {
		return this.pdpModel.get();
	}

	public RoadModel getRoadModel() {
		return super.getRoadModel();
	}
}
