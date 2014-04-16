package ra.parcel;

import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

/**
 * Parcel based off the adaptive slack parcel that simply inhibits re-auctions afer a loop has occurred. Just for
 * benchmarking and testing, not a very useful strategy
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class InhibitAfterLoop extends AdaptiveSlackReAuctionableParcel {
    private boolean inhibit = false;

    public InhibitAfterLoop(ParcelDTO pDto, float standardDeviation) {
        super(pDto, standardDeviation);
    }

    @Override
    public boolean shouldChangeOwner() {
        // If super doesn't allow to re-auction, neither should we
        if (!super.shouldChangeOwner())
            return false;

        if (inhibit)
            return false;

        int last = getOwnerHistory().size() - 1;
        if (getOwnerHistory().size() != 1 && getOwnerHistory().get(last) == getOwnerHistory().get(last - 1)) {
            inhibit = true;
            return false;
        }

        return true;
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator(final float numberStandardDeviations) {
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new InhibitAfterLoop(event.parcelDTO, numberStandardDeviations));
                return true;
            }

            @Override
            public String toString() {
                return "InhibitAfterLoop" + numberStandardDeviations + "STD";
            }
        };
    }
}
