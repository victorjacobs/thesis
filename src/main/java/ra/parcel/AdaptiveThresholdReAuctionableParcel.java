package ra.parcel;

import common.auctioning.ReAuctionableParcel;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

import java.util.Collection;

/**
 * ReAuctionableParcel that mimics {@link ra.evaluator.AdaptiveLocalStateEvaluator}. Meaning that instead of the Truck keeping
 * track of the slack, the parcel will.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class AdaptiveThresholdReAuctionableParcel extends AgentParcel {
    private double mean;
    private int n;
    private double M2;
    private double variance;

    public AdaptiveThresholdReAuctionableParcel(ParcelDTO pDto) {
        super(pDto);

        n = 0;
        mean = 0;
        M2 = 0;
        variance = 0;
    }

    @Override
    public boolean shouldChangeOwner() {
        return super.shouldChangeOwner();
    }

    @Override
    public void update(double slack) {
        double delta;

        n++;
        delta = slack - mean;
        mean += delta / n;
        M2 += delta * (slack - mean);

        if (n > 1)
            variance = M2 / (n - 1);
    }

    double getStandardDeviation() {
        return Math.sqrt(variance);
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
                return "AdaptiveThresholdReAuctionableParcel";
            }
        };
    }
}
