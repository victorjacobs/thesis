package common.results;

import com.google.common.collect.ImmutableList;
import common.auctioning.ReAuctionableParcel;
import rinde.sim.core.model.AbstractModel;
import rinde.sim.util.SupplierRng;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Model that tracks all {@link common.auctioning.ReAuctionableParcel}s created by the simulation. This is needed to
 * later get statistics from them.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ParcelTrackerModel extends AbstractModel<ReAuctionableParcel> {

	private List<ReAuctionableParcel> parcels;

	private ParcelTrackerModel() {
		parcels = newLinkedList();
	}

	public ImmutableList<ReAuctionableParcel> getParcels() {
		return ImmutableList.copyOf(parcels);
	}

	@Override
	public boolean register(ReAuctionableParcel element) {
		parcels.add(element);

		return true;
	}

	@Override
	public boolean unregister(ReAuctionableParcel element) {
		throw new NotImplementedException();
	}

	public static SupplierRng<ParcelTrackerModel> supplier() {
		return new SupplierRng.DefaultSupplierRng<ParcelTrackerModel>() {
			@Override
			public ParcelTrackerModel get(long seed) {
				return new ParcelTrackerModel();
			}
		};
	}
}
