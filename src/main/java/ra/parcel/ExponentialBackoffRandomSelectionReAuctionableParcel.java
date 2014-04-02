package ra.parcel;

import common.truck.Bidder;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

import java.util.Random;

/**
 * This parcel extends the {@link AdaptiveSlackReAuctionableParcel} in such a way that it implements an
 * exponential backoff scheme when auctioning. I.e. when the parcel is repeatedly auctioned and won by the same
 * agent, it will try to auction itself less.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ExponentialBackoffRandomSelectionReAuctionableParcel extends AdaptiveSlackReAuctionableParcel {
    private int backoff;
    private float nextBackoff;
    private float step;
    private Random rng;
    private int randomPercentage;

    public ExponentialBackoffRandomSelectionReAuctionableParcel(ParcelDTO pDto, float backoffStep, int randPercentage) {
        super(pDto, 1);
        backoff = 0;
        step = backoffStep;

        nextBackoff = step;
        rng = new Random();
        this.randomPercentage = randPercentage;
    }

    @Override
    public boolean shouldChangeOwner() {
        if (rng.nextInt(100) >= randomPercentage)
            return false;

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
        return getCreator(1);
    }

    /**
     * Gets a creator for this parcel.
     *
     * @param backoffStep Step for exponential backoff
     * @return Creator for this parcel
     */
    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator(final float backoffStep,
                                                                         final int randPercentage) {
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new ExponentialBackoffRandomSelectionReAuctionableParcel(event.parcelDTO, backoffStep, randPercentage));
                return true;
            }

            @Override
            public String toString() {
                return "ExponentialBackoffRandom" + randPercentage + "Selection" + backoffStep + "step";
            }
        };
    }
}
