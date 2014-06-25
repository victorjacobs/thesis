package common.results;

import com.google.common.collect.ImmutableList;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.core.model.AbstractModel;
import rinde.sim.util.SupplierRng;

import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Model that tracks all {@link ra.parcel.ReAuctionableParcel}s created by the simulation. This is needed to
 * later get statistics from them.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ParcelTrackerModel extends AbstractModel<ReAuctionableParcel> {

	private List<ReAuctionableParcel> parcels;

	private ParcelTrackerModel() {
		parcels = newLinkedList();
    }

	@Override
	public boolean register(ReAuctionableParcel element) {
		parcels.add(element);

		return true;
	}

    @Override
    public boolean unregister(ReAuctionableParcel element) {
        parcels.remove(element);
        System.out.println("WARNING: parcel removed from system");
        return true;
    }

    public ImmutableList<ReAuctionableParcel> getParcels() {
        return ImmutableList.copyOf(parcels);
    }

    /**
     * Returns total number of re-auctions that occured in the simulation run.
     *
     * @return Total re-auctions of all parcels in the simulation
     */
    public int getTotalReAuctions() {
        int total = 0;

        for (ReAuctionableParcel par : parcels) {
            total += par.getOwnerHistory().size();
        }

        return total;
    }

    public float getOverallEfficiency() {
        float sum = 0;
        int divider = 0;

        for (ReAuctionableParcel p : parcels) {
            if (Float.isNaN(p.getPercentageUsefulReAuctions()) || p.getNumberReAuctions() == 0) continue;

            sum += p.getPercentageUsefulReAuctions();
            divider++;
        }

        return sum / divider;
    }

    public float getReAuctionsPerParcel() {
        return (float) getTotalReAuctions() / parcels.size();
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
