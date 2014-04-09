package ra.evaluator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import common.truck.StateObserver;
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
public class AgentParcelSlackEvaluatorUpdateOnChange extends SlackEvaluator implements StateObserver {
    Map<DefaultParcel, Double> oldSlacks;

    public AgentParcelSlackEvaluatorUpdateOnChange(long seed) {
        super(seed);
    }

    @Override
    public ImmutableSet<DefaultParcel> evaluateState(long time) {
        // Return everything, the parcel has responsability of deciding on re-auction
        return getTruck().getParcels();
    }

    @Override
    public void notify(long time) {
        Map<DefaultParcel, Double> slacks = calculateSlackForState(time);
        AgentParcel ap;
        // Should only update the parcels that changed
        if (oldSlacks == null) {
            oldSlacks = slacks;

            // Update everything
            for (DefaultParcel p : slacks.keySet()) {
                ap = (AgentParcel) p;
                ap.update(slacks.get(p));
            }
        } else {
            // Compare and see what parcels were added
            for (DefaultParcel p : Sets.difference(slacks.keySet(), oldSlacks.keySet())) {
                ap = (AgentParcel) p;
                ap.update(slacks.get(p));
            }
        }
    }

    public static SupplierRng<? extends AgentParcelSlackEvaluatorUpdateOnChange> supplier() {
        return new SupplierRng.DefaultSupplierRng<AgentParcelSlackEvaluatorUpdateOnChange>() {
            @Override
            public AgentParcelSlackEvaluatorUpdateOnChange get(long seed) {
                return new AgentParcelSlackEvaluatorUpdateOnChange(seed);
            }
        };
    }
}
