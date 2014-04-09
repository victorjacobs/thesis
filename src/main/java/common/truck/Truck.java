package common.truck;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import common.truck.route.RoutePlanner;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.core.TimeLapse;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.core.model.road.RoadModel;
import rinde.sim.event.Event;
import rinde.sim.event.Listener;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.RouteFollowingVehicle;
import rinde.sim.pdptw.common.VehicleDTO;
import rinde.sim.util.fsm.StateMachine;

import java.util.HashSet;
import java.util.Iterator;
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
 * handle in negotiation with other Trucks through the {@link common.auctioning.Auctioneer} while the latter can make
 * independent decisions based on just the state.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Truck extends RouteFollowingVehicle implements Listener, SimulatorUser {

	// State
	private Set<DefaultParcel> parcels;			// All parcels that will be/are(!) being handled by this truck
	private Set<DefaultParcel> fixedParcels;	// Parcels (plural!) that shouldn't be removed from the state
	// Components
	private List<StateObserver> stateObservers;
	private List<StateEvaluator> stateEvaluators;
	private Bidder bidder;					// Only needed to register in simulator when it starts
	private RoutePlanner routePlanner;    // TODO RP is both set here and in the stateObservers -> no longer needed?

	private long ticks = 0;

	/**
	 * Initializes the vehicle.
	 * @param pDto The {@link rinde.sim.pdptw.common.VehicleDTO} that defines this vehicle.
	 * @param rp {@link common.truck.route.RoutePlanner} that controls the RouteFollowingVehicle underneath this Truck
	 * @param b {@link common.truck.Bidder} that manages the parcels for which this truck is responsable
	 */
	public Truck(VehicleDTO pDto, RoutePlanner rp, Bidder b) {
		super(pDto, false);        // TODO no idea what this flag does
		pdpModel = Optional.absent();

		parcels = newLinkedHashSet();
		fixedParcels = newLinkedHashSet();
		stateObservers = newLinkedList();
		stateEvaluators = newLinkedList();

		bindBidder(b);
		bindRoutePlanner(rp);

		// Register self as listener to stateMachine events
		stateMachine.getEventAPI().addListener(this, StateMachine.StateMachineEvent.STATE_TRANSITION);
	}

	// Setup
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
		checkState(this.bidder == null, "Bidder already bound to Truck");

		this.bidder = bidder;
		bidder.bindTruck(this);
	}

	/**
	 * Doubly binds a route planner to this truck. Result is that the truck has a reference to the route planner and
	 * that the route planner is subscribed to state change notifications from the Truck.
	 * @param routePlanner
	 */
	private void bindRoutePlanner(RoutePlanner routePlanner) {
		checkState(this.routePlanner == null, "Route planner already bound to Truck");

		routePlanner.setTruck(this);
		this.routePlanner = routePlanner;
		addStateObserver(routePlanner);
	}

	// Manage state

	/**
	 * Adds a parcel to this truck. This means that, for now, the truck is committed to servicing the parcels unless
	 * a {@link StateEvaluator} wants to remove it. This method also calls the {@link StateObserver}s that are
	 * registered to inform them that a new parcel was added.
	 * @param par Parcel that is to be added to the truck
	 */
	void addParcel(DefaultParcel par) {
		checkArgument(!parcels.contains(par));
		parcels.add(par);
		notifyChange();
	}

	/**
	 * Removes a parcel from this truck. This will notify connected observers that a parcel was removed. This will
	 * make sure that the parcel doesn't just disappear but will get re-assigned to another truck.
     *
	 * @param par Parcel that was removed
     * @return Whether or not parcel was successfully removed
	 */
    // TODO why the hell is this a DefaultParcel?
	private void removeParcel(DefaultParcel par) {
		checkState(par instanceof ReAuctionableParcel, "Parcel needs to be re-auctionable in order to remove it from " +
				"truck");
		checkState(parcels.contains(par), "Parcel not assigned to truck");
		if (fixedParcels.contains(par))
            return;

        ReAuctionableParcel rParcel = (ReAuctionableParcel) par;

        if (rParcel.shouldChangeOwner()) {
            // First remove the parcel from local state + update route planner etc, then change owner
            parcels.remove(par);
            fixedParcels.remove(par);
            notifyChange();

            rParcel.changeOwner(getCurrentTime().getTime());
        }
	}

	/**
	 * Notifies listeners that state has changed.
	 */
	private void notifyChange() {
		for (StateObserver l : stateObservers) {
			l.notify(getCurrentTime().getTime());
		}
	}

	/**
	 * Gets immutable copy of the internal state of this truck. This contains *all* parcels which are not yet
	 * completely handled. This means both stuff in cargo as stuff not yet picked up.
	 * @return Immutable copy of internal state of this truck
	 */
	public ImmutableSet<DefaultParcel> getParcels() {
		return ImmutableSet.copyOf(parcels);
	}

	/**
	 * Loops over all {@link common.truck.StateEvaluator}s connected to the truck after every tick and executes the
	 * ones which need executing.
	 */
	@Override
	public void afterTick(TimeLapse time) {
		for (StateEvaluator ev : stateEvaluators) {
			if (ev.shouldReEvaluate(ticks)) {
				ImmutableSet<DefaultParcel> parcelsToRemove = ev.evaluateState(getCurrentTime().getTime());

				for (DefaultParcel par : parcelsToRemove) {
					removeParcel(par);
				}
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

	/**
	 * Updates internal state of the truck when the fsm behind RouteFollowingVehicle changes state.
     *
	 * @param e
	 */
	@Override
	public void handleEvent(Event e) {
		try {
			@SuppressWarnings("unchecked")
			StateMachine.StateTransitionEvent<StateEvent, RouteFollowingVehicle> event =
					(StateMachine.StateTransitionEvent<StateEvent, RouteFollowingVehicle>) e;

			if (event.event == DefaultEvent.GOTO || event.event == DefaultEvent.ARRIVED) {
				DefaultParcel cur = getRoute().iterator().next();
				// RouteFollowingVehicle decided to go to certain parcel

				// If the next stop on route is not yet in cargo (i.e. not underway to delivery location),
				// add it to list of parcels that are fixed in the state
				if (pdpModel.get().getParcelState(cur) != PDPModel.ParcelState.IN_CARGO) {
					fixedParcels.add(cur);
				}
			} else if (event.event == DefaultEvent.DONE) {
				// RouteFollowingVehicle is done servicing location, this might be when parcel is delivered or picked
				// up. In both cases, inform route planner

				Iterator<DefaultParcel> it = parcels.iterator();

				while (it.hasNext()) {
					if (pdpModel.get().getParcelState(it.next()) == PDPModel.ParcelState.DELIVERED)
						it.remove();
				}

				// Inform routeplanner to go to next goal
				// Since contents of this truck are added in the route planner, temporarily remove them here
				// TODO only notify routePlanner -> this breaks the concept of observer pattern!!
				Set<DefaultParcel> newState = new HashSet<DefaultParcel>(parcels);
				newState.removeAll(getContents());

				routePlanner.update(newState, getCurrentTime().getTime());
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

	public PDPModel getPdpModel() {
		return this.pdpModel.get();
	}

	public RoadModel getRoadModel() {
		return super.getRoadModel();
	}

    /**
     * Gets contents of this truck from the pdpModel.
     *
     * @return Set of parcels that is contained in cargo
     */
	public ImmutableSet<Parcel> getContents() {
		return this.pdpModel.get().getContents(this);
	}

    /**
     * Gets position of this truck from the road model
     *
     * @return Position of this truck
     */
	public Point getPosition() {
		return super.getRoadModel().getPosition(this);
	}
}
