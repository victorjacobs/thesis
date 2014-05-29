package ra.parcel;

import common.truck.Bidder;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

/**
 * Parcel that does exponential backoff, but doesn't track heuristic values
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ExponentialBackoffAgentParcel extends AgentParcel {
    private int backoff;
    private float nextBackoff;
    private float step;

    public ExponentialBackoffAgentParcel(ParcelDTO pDto) {
        super(pDto);

        step = 2;
    }

    @Override
    public boolean shouldChangeOwner() {
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
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new ExponentialBackoffAgentParcel(event.parcelDTO));
                return true;
            }

            @Override
            public String toString() {
                return "ExponentialBackoffParcel";
            }
        };
    }

}
