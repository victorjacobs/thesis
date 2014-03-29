package ra.parcel;

import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

/**
 * ReAuctionableParcel that mimics {@link ra.evaluator.AdaptiveSlackEvaluator}. Meaning that instead of the Truck keeping
 * track of the slack, the parcel will.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class AdaptiveSlackReAuctionableParcel extends AgentParcel {
    private final float numberStandardDeviations;
    private double mean;
    private int n;
    private double M2;
    private double variance;

    private double lastSlack;

    public AdaptiveSlackReAuctionableParcel(ParcelDTO pDto, float numberStandardDeviations) {
        super(pDto);
        this.numberStandardDeviations = numberStandardDeviations;

        n = 0;
        mean = 0;
        M2 = 0;
        variance = 0;
    }

    @Override
    public boolean shouldChangeOwner() {
        return (lastSlack < mean - numberStandardDeviations * getStandardDeviation());
    }

    @Override
    public void update(double slack) {
        super.update(slack);

        n++;
        double delta = lastSlack - mean;
        mean += delta / n;
        M2 += delta * (lastSlack - mean);

        if (n > 1)
            variance = M2 / (n - 1);

        lastSlack = slack;
    }

    double getStandardDeviation() {
        return Math.sqrt(variance);
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
        return getCreator(1);
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator(final float numberStandardDeviations) {
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new AdaptiveSlackReAuctionableParcel(event.parcelDTO, numberStandardDeviations));
                return true;
            }

            @Override
            public String toString() {
                return "AdaptiveSlack" + numberStandardDeviations + "STD";
            }
        };
    }
}
