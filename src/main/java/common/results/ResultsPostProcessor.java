package common.results;

import com.google.common.collect.ImmutableList;
import ra.parcel.AgentParcel;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.experiment.PostProcessor;

/**
 * {@link rinde.sim.pdptw.experiment.PostProcessor} that simply returns all the parcels tracked by the {@link
 * common.results.ParcelTrackerModel} for certain simulation. Might merge with {@link common.results.ResultsProcessor}.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ResultsPostProcessor implements PostProcessor {
	@Override
    @SuppressWarnings("all")    // Believe me, compiler, everything will be fine
	public ImmutableList<ReAuctionableParcel> collectResults(Simulator sim) {
		try {
            ParcelTrackerModel tracker = sim.getModelProvider().getModel(ParcelTrackerModel.class);
            AgentParcel ap;

            return tracker.getParcels();
        } catch (RuntimeException e) {
            System.err.println(e);
            System.err.println("WARNING: No parcel tracker registered, returning empty list (check configuration)");

            return ImmutableList.of();
        }
	}
}
