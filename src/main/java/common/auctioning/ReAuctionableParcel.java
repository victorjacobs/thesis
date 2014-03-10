package common.auctioning;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import common.truck.Bidder;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

/**
 * Extends {@link DefaultParcel} with a reference to an {@link Auctioneer}. This allows the parcel to change owners.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctionableParcel extends DefaultParcel {

	private Optional<Auctioneer> auctioneer;
    private boolean reAuctionPrevented = false;
    protected List<Bidder> ownerHistory;

	public ReAuctionableParcel(ParcelDTO pDto) {
		super(pDto);
		auctioneer = Optional.absent();
		ownerHistory = newLinkedList();
	}

    /**
     * Set Auctioneer that will be used in reauctioning.
     *
     * @param auct Auctioneer that owns the parcel
     */
	public final void setAuctioneer(Auctioneer auct) {
		checkState(!auctioneer.isPresent(), "Auctioneer already set");

		auctioneer = Optional.of(auct);
	}

    /**
     * Is the parcel owned by an auctioneer?
     *
     * @return The parcel is owned by an auctioneer
     */
	public final boolean hasAuctioneer() {
		return auctioneer.isPresent();
	}

    public boolean shouldChangeOwner() {
        return true;
    }

    /**
     * Attempt to change owner of the parcel.
     * NOTE: FIRST update local state BEFORE calling this.
     *
     * @param time Simulation time
     * @return Whether re-auction is allowed or not (decided by the parcel itself)
     */
	public final boolean changeOwner(long time) {
        if (this.reAuctionPrevented || !shouldChangeOwner()) {
            this.reAuctionPrevented = true;
            return false;
        }

		checkState(auctioneer.isPresent(), "Auctioneer needed to change owner");

		ownerHistory.add(auctioneer.get().auction(this, time));

        return true;
	}

    @Override
    public String toString() {
        return super.toString();
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
		return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
			@Override
			public boolean create(Simulator sim, AddParcelEvent event) {
				sim.register(new ReAuctionableParcel(event.parcelDTO));
				return true;
			}

            @Override
            public String toString() {
                return "ReAuctionableParcel";
            }
        };
	}

    // Stats
    public final ImmutableList<Bidder> getOwnerHistory() {
        return ImmutableList.copyOf(ownerHistory);
    }

    public final boolean reAuctionPrevented() {
        return reAuctionPrevented;
    }
}
