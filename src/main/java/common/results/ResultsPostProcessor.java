package common.results;

import com.google.common.collect.ImmutableList;
import common.auctioning.ReAuctionableParcel;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.experiment.PostProcessor;

/**
 * {@link rinde.sim.pdptw.experiment.PostProcessor} that simply returns all the parcels tracked by the {@link
 * common.results.ParcelTrackerModel} for certain simulation. Might merge with {@link common.results.ResultsProcessor}.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ResultsPostProcessor implements PostProcessor {
	@Override
	public ImmutableList<ReAuctionableParcel> collectResults(Simulator sim) {
		ParcelTrackerModel tracker = sim.getModelProvider().getModel(ParcelTrackerModel.class);

		return tracker != null ? tracker.getParcels() : null;
	}
}
