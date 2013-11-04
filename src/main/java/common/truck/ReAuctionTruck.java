package common.truck;

import com.google.common.collect.ImmutableSet;
import rinde.logistics.pdptw.mas.route.RoutePlanner;
import rinde.sim.core.SimulatorAPI;
import rinde.sim.core.SimulatorUser;
import rinde.sim.event.Event;
import rinde.sim.event.Listener;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.RouteFollowingVehicle;
import rinde.sim.pdptw.common.VehicleDTO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctionTruck extends RouteFollowingVehicle implements Listener, SimulatorUser {

	private Set<DefaultParcel> state;
	private DefaultParcel nextParcel;
	private List<StateChangeListener> stateChangeListeners;
	private Communicator comm;
	private RoutePlanner routePlanner;

	/**
	 * Initializes the vehicle.
	 *
	 * @param pDto                      The {@link rinde.sim.pdptw.common.VehicleDTO} that defines this vehicle.
	 * @param allowDelayedRouteChanging This boolean changes the behavior of the
	 *                                  {@link #setRoute(java.util.Collection)} method.
	 */
	public ReAuctionTruck(VehicleDTO pDto, boolean allowDelayedRouteChanging) {
		super(pDto, allowDelayedRouteChanging);
	}


	public void addStateListener(StateChangeListener l) {
		stateChangeListeners.add(l);
	}

	private void stateChanged() {
		for (StateChangeListener l : stateChangeListeners) {
			l.notifyStateChanged(ImmutableSet.copyOf(state), getCurrentTime().getTime());
		}
	}

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

	@Override
	public void setRoute(Collection<DefaultParcel> r) {
		super.setRoute(r);
		if (r.size() > 0)
			nextParcel = r.iterator().next();
	}

	@Override
	public void handleEvent(Event e) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setSimulator(SimulatorAPI api) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
