package common.auctioning;

import com.google.common.base.Optional;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

import static com.google.common.base.Preconditions.checkState;

/**
 * Extends {@link DefaultParcel} with a reference to an {@link Auctioneer}. This allows the parcel to change owners.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctionableParcel extends DefaultParcel {

	private Optional<Auctioneer> auctioneer;

	public ReAuctionableParcel(ParcelDTO pDto) {
		super(pDto);
		auctioneer = Optional.absent();
	}

	public void setAuctioneer(Auctioneer auct) {
		checkState(!auctioneer.isPresent(), "Auctioneer already set");

		auctioneer = Optional.of(auct);
	}

	public boolean hasAuctioneer() {
		return auctioneer.isPresent();
	}

	public void changeOwner(long time) {
		checkState(auctioneer.isPresent(), "Auctioneer needed to change owner");

		auctioneer.get().auction(this, time);
	}

	public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
		return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
			@Override
			public boolean create(Simulator sim, AddParcelEvent event) {
				sim.register(new ReAuctionableParcel(event.parcelDTO));
				return true;
			}
		};
	}
}
