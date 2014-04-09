package ra.evaluator;

import com.google.common.collect.ImmutableSet;
import ra.parcel.AgentParcel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * This is a *dumb* state evaluator. It moves the responsability of deciding on a re-auction to the parcel itself.
 * Therefore the implementation of {@link #evaluateState(long)} simply returns all parcels. It therefore should be
 * paired with a parcel that can handle this responsability (see {@link ra.parcel.AdaptiveSlackReAuctionableParcel}
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class AgentParcelSlackEvaluator extends SlackEvaluator {
    public AgentParcelSlackEvaluator(long seed) {
        super(seed);
    }

    @Override
    @SuppressWarnings("all")
    public ImmutableSet<DefaultParcel> evaluateState(long time) {
        // Update slack in parcels and then simply return everything
        Map<DefaultParcel,Double> slacks = calculateSlackForState(time);
        AgentParcel rp;

        for (DefaultParcel par : slacks.keySet()) {
            checkState(par instanceof AgentParcel, "AgentParcelSlackEvaluator only works with AgentParcels");

            rp = (AgentParcel) par;

            rp.update(slacks.get(par));
        }

        return ImmutableSet.copyOf(slacks.keySet());
    }

    public static SupplierRng<? extends AgentParcelSlackEvaluator> supplier() {
        return new SupplierRng.DefaultSupplierRng<AgentParcelSlackEvaluator>() {
            @Override
            public AgentParcelSlackEvaluator get(long seed) {
                return new AgentParcelSlackEvaluator(seed);
            }
        };
    }
}
