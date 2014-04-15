package ra.parcel;

import common.truck.Bidder;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

/**
 * This parcel extends the {@link ra.parcel.AdaptiveSlackReAuctionableParcel} in such a way that it implements an
 * exponential backoff scheme when auctioning. I.e. when the parcel is repeatedly auctioned and won by the same
 * agent, it will try to auction itself less.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ExponentialBackoffSlackReAuctionableParcel extends AdaptiveSlackReAuctionableParcel {
    private int backoff;
    private float nextBackoff;
    private final float step;

    public ExponentialBackoffSlackReAuctionableParcel(ParcelDTO pDto, float numberStandardDeviations,
                                                      float backoffStep) {
        super(pDto, numberStandardDeviations);
        backoff = 0;
        step = backoffStep;

        nextBackoff = step;
    }

    @Override
    public boolean shouldChangeOwner() {
        // If super doesn't allow to re-auction, neither should we
        if (!super.shouldChangeOwner())
            return false;

        // Backing off
        if (backoff > 0) {
            backoff--;

            // If this reduces backoff to 0, allow to auction
            return backoff == 0;
        }

        // TODO this seems too convoluted, why not simply check history[last] == history[last - 1]?
        int loopCount = 0;
        int i = getOwnerHistory().size() - 2;
        Bidder currentBidder = getOwnerHistory().get(getOwnerHistory().size() - 1);
        while (i >= 0 && currentBidder == getOwnerHistory().get(i)) {
            loopCount++;
            i--;
        }

        if (loopCount == 0) {
            // Changed owner, reset
            nextBackoff = step;
            return true;
        } else {
            // Looping to self
            backoff = Math.round(nextBackoff);
            nextBackoff *= step;
            return false;
        }
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
        return getCreator(1, 2);
    }

    /**
     * Gets a creator for this parcel.
     *
     * @param numberStandardDeviations Number of standard deviations used as threshold for re-auctioning
     * @param backoffStep Step for exponential backoff
     * @return Creator for this parcel
     */
    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator(final float numberStandardDeviations,
                                                                         final float backoffStep) {
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new ExponentialBackoffSlackReAuctionableParcel(event.parcelDTO,
                        numberStandardDeviations, backoffStep));
                return true;
            }

            @Override
            public String toString() {
                return "ExponentialBackoff" + numberStandardDeviations + "STD" + backoffStep + "step";
            }
        };
    }
}
