package ra.parcel;

import common.auctioning.ReAuctionableParcel;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

/**
 * Extends the default ReAuctionableParcel to limit number of reauctions to 20.
 *
 * Created by victor on 10/03/14.
 */
public class FixedThresholdReAuctionableParcel extends ReAuctionableParcel {
    public FixedThresholdReAuctionableParcel(ParcelDTO pDto) {
        super(pDto);
    }

    @Override
    public boolean shouldChangeOwner() {
        return (ownerHistory.size() <= 20);
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new FixedThresholdReAuctionableParcel(event.parcelDTO));
                return true;
            }

            @Override
            public String toString() {
                return "FixedThresholdReAuctionableParcel";
            }
        };
    }
}
