package ra.parcel;

import common.truck.Bidder;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

import java.util.Collections;

/**
 * This parcel extends the {@link ra.parcel.AdaptiveSlackReAuctionableParcel} in such a way that it implements an
 * exponential backoff scheme when auctioning. I.e. when the parcel is repeatedly auctioned and won by the same
 * agent, it will try to auction itself less.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ExponentialBackoffSlackReAuctionableParcel extends AdaptiveSlackReAuctionableParcel {
    private int backoff;
    private int step;

    public ExponentialBackoffSlackReAuctionableParcel(ParcelDTO pDto, float numberStandardDeviations) {
        super(pDto, numberStandardDeviations);
        backoff = 0;
        step = 2;
    }

    @Override
    public boolean shouldChangeOwner() {
        // If super doesn't allow to re-auction, neither should we
        if (!super.shouldChangeOwner())
            return false;

        System.out.println(backoff);

        // Backing off
        if (backoff > 0) {
            backoff--;

            // If this reduces backoff to 0, allow to auction
            return backoff == 0;
        }

        int loopCount = 0;
        int i = getOwnerHistory().size() - 2;
        Bidder currentBidder = getOwnerHistory().get(getOwnerHistory().size() - 1);
        while (i >= 0 && currentBidder == getOwnerHistory().get(i)) {
            loopCount++;
            i--;
        }

        if (loopCount == 0) {
            // Changed owner, reset
            step = 2;
            return true;
        } else {
            // Looping to self
            backoff = step;
            step *= 2;
            return false;
        }
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
        return getCreator(1);
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator(final float numberStandardDeviations) {
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new ExponentialBackoffSlackReAuctionableParcel(event.parcelDTO, numberStandardDeviations));
                return true;
            }

            @Override
            public String toString() {
                return "AdaptiveSlack" + numberStandardDeviations + "STD";
            }
        };
    }
}
