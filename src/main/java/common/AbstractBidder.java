/**
 *
 */
package common;

import com.google.common.base.Optional;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.road.RoadModel;
import rinde.sim.event.Event;
import rinde.sim.event.EventDispatcher;
import rinde.sim.event.Listener;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.DefaultVehicle;
import rinde.sim.pdptw.common.PDPRoadModel;

import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Collections.unmodifiableSet;

/**
 * Basic implementation for {@link Bidder}.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 */
public abstract class AbstractBidder implements Bidder {

	/**
	 * The set of parcels that are assigned to this bidder.
	 */
	protected final Set<DefaultParcel> assignedParcels;

	/**
	 * The event dispatcher.
	 */
	protected final EventDispatcher eventDispatcher;

	/**
	 * The road model.
	 */
	protected Optional<PDPRoadModel> roadModel;

	/**
	 * The pdp model.
	 */
	protected Optional<PDPModel> pdpModel;

	/**
	 * The vehicle for which this bidder operates.
	 */
	protected Optional<DefaultVehicle> vehicle;

	/**
	 * Initializes bidder.
	 */
	public AbstractBidder() {
		assignedParcels = newLinkedHashSet();
		eventDispatcher = new EventDispatcher(CommunicatorEventType.values());
		roadModel = Optional.absent();
		pdpModel = Optional.absent();
		vehicle = Optional.absent();
	}

	@Override
	public void addUpdateListener(Listener l) {
		eventDispatcher.addListener(l, CommunicatorEventType.CHANGE);
	}

	// ignore
	@Override
	public void waitFor(DefaultParcel p) {}

	@Override
	public void claim(DefaultParcel p) {
		checkArgument(assignedParcels.contains(p));
		assignedParcels.remove(p);
	}

	@Override
	public final Collection<DefaultParcel> getParcels() {
		return unmodifiableSet(assignedParcels);
	}

	@Override
	public void receiveParcel(DefaultParcel p) {
		assignedParcels.add(p);
		eventDispatcher
				.dispatchEvent(new Event(CommunicatorEventType.CHANGE, this));
	}

	@Override
	public final void init(RoadModel rm, PDPModel pm, DefaultVehicle v) {
		roadModel = Optional.of((PDPRoadModel) rm);
		pdpModel = Optional.of(pm);
		vehicle = Optional.of(v);
		afterInit();
	}

	/**
	 * This method can optionally be overridden to execute additional code right
	 * after {@link #init(RoadModel, PDPModel, DefaultVehicle)} is called.
	 */
	protected void afterInit() {}
}
