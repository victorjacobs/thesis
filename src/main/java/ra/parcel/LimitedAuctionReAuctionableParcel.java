package ra.parcel;

import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

/**
 * Extends the default ReAuctionableParcel to limit number of reauctions to 20.
 *
 * Created by victor on 10/03/14.
 */
public class LimitedAuctionReAuctionableParcel extends AgentParcel {
    private final int threshold;

    public LimitedAuctionReAuctionableParcel(ParcelDTO pDto) {
        this(pDto, 20);
    }

    public LimitedAuctionReAuctionableParcel(ParcelDTO pDto, int threshold) {
        super(pDto);
        this.threshold = threshold;
    }

    @Override
    public boolean shouldChangeOwner() {
        return (ownerHistory.size() <= threshold);
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
        return getCreator(20);
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator(final int threshold) {
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new LimitedAuctionReAuctionableParcel(event.parcelDTO, threshold));
                return true;
            }

            @Override
            public String toString() {
                return "Max" + threshold + "Reauctions";
            }
        };
    }
}
