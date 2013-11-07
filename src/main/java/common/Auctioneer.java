package common;

import com.google.common.base.Optional;
import common.truck.Bidder;
import rinde.sim.core.model.AbstractModel;
import rinde.sim.core.model.ModelProvider;
import rinde.sim.core.model.ModelReceiver;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.PDPModelEvent;
import rinde.sim.event.Event;
import rinde.sim.event.Listener;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newLinkedHashSet;

/**
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO binding with model
public class Auctioneer extends AbstractModel<Bidder> implements ModelReceiver {
	private Set<Bidder> bidders;

	public Auctioneer() {
		bidders = newLinkedHashSet();
	}

	public void auction(DefaultParcel par, long time) {
		auction(par, 0, time);
	}

	public void auction(DefaultParcel par, double reservationPrice, long time) {
		checkState(!bidders.isEmpty(), "There are no bidders..");

		final Iterator<Bidder> it = bidders.iterator();
		Bid bestBid  = it.next().getBidFor(par, time);
		Bid curBid;
		while (it.hasNext()) {
			final Bidder cur = it.next();
			curBid = cur.getBidFor(par, time);
			if (curBid.compareTo(bestBid) < 0) {
				bestBid = curBid;
			}
		}

		bestBid.receiveParcels();
	}

	@Override
	public boolean register(Bidder element) {
		bidders.add(element);
		element.bindAuctioneer(this);
		return true;
	}

	@Override
	public boolean unregister(Bidder element) {
		throw new UnsupportedOperationException();	// TODO for now
	}

	@Override
	public void registerModelProvider(ModelProvider mp) {
		final PDPModel pm = Optional.fromNullable(mp.getModel(PDPModel.class))
				.get();
		pm.getEventAPI().addListener(new Listener() {
			@Override
			public void handleEvent(Event e) {
				final PDPModelEvent event = (PDPModelEvent) e;
				checkArgument(event.parcel instanceof DefaultParcel,
						"This class is only compatible with DefaultParcel and subclasses.");
				final DefaultParcel dp = (DefaultParcel) event.parcel;
				auction(dp, event.time);
			}
		}, PDPModel.PDPModelEventType.NEW_PARCEL);
	}


	public static SupplierRng<Auctioneer> supplier() {
		return new SupplierRng.DefaultSupplierRng<Auctioneer>() {
			@Override
			public Auctioneer get(long seed) {
				return new Auctioneer();
			}
		};
	}
}
