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

import java.util.List;
import java.util.Set;

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
	private Set<DefaultParcel> state;			// All parcels that will be/are(!) being handled by this truck
	private Set<DefaultParcel> fixedParcels;	// Parcels (plural!) that shouldn't be removed from the state
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
	 * @param rp
	 * @param b
	 */
	public Truck(VehicleDTO pDto, RoutePlanner rp, Bidder b) {
		super(pDto, false);		// TODO no idea what this flag does
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
	 * @param routePlanner
	 */
	private void bindRoutePlanner(RoutePlanner routePlanner) {
		checkState(routePlanner != null, "Route planner already bound to Truck");

		routePlanner.bindTruck(this);
		this.routePlanner = routePlanner;	// TODO is this needed?
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
		for (StateObserver l : stateObservers) {
			l.notifyStateChanged(ImmutableSet.copyOf(state), getCurrentTime().getTime());
		}
	}

	public ImmutableSet<DefaultParcel> getParcels() {
		return ImmutableSet.copyOf(state);
	}

	@Override
	public void afterTick(TimeLapse time) {
		for (StateObserver o : stateObservers) {
			if (o.reEvaluateState(ticksSinceLastReEvaluation, getCurrentTime().getTime())) {
				ticksSinceLastReEvaluation = 0;	// Reset
				return;
			}
		}

		ticksSinceLastReEvaluation++;
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

			// TODO is this safe? Normally it is.
			DefaultParcel cur = getRoute().iterator().next();

			if (event.event == StateEvent.GOTO) {
				// Update state
				if (pdpModel.get().getParcelState(cur) == PDPModel.ParcelState.IN_CARGO) {
					// Remove cur from state, routeplanner will ensure proper delivery of stuff already in cargo
					System.out.println(toString() + " Removing " + cur + " from state");
					state.remove(cur);
				} else {
					// Not yet in cargo, but commited to going there
					System.out.println(toString() + " Fixing " + cur);
					fixedParcels.add(cur);
				}
			} else if (event.event == StateEvent.DONE) {
				// TODO this is a lot of overhead since this will re-invoke the solver
				//removeParcel(cur);
				state.remove(cur);
				fixedParcels.remove(cur);
				routePlanner.update(ImmutableSet.copyOf(state), getCurrentTime().getTime());
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
