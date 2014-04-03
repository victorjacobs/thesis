package common.results.measures;

import common.results.Result;
import common.results.ResultDirectory;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.List;
import java.util.Map;

/**
 * This is the same as {@link common.results.measures.MaxEdgesOwnerGraph} except that it generates files for
 * every parcel in the simulation, neatly organised in directories.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class AllWeighedOwnerGraph extends MaxEdgesOwnerGraph {
    @Override
    public Result evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        ResultDirectory<String> topDir = new ResultDirectory<String>("allGraphs");
        ResultDirectory<String> configDir;
        ResultDirectory<String> runDir;
        int runNumber;

        for (String config : resultBins.keySet()) {
            configDir = new ResultDirectory<String>(config);
            topDir.addResult(configDir);

            runNumber = 1;
            for (Experiment.SimulationResult run : resultBins.get(config)) {
                runDir = new ResultDirectory<String>(Integer.toString(runNumber));
                configDir.addResult(runDir);

                for (ReAuctionableParcel par : getParcelsFromRun(run)) {
                    runDir.addResult(getWriter(Integer.toString(runNumber) + Integer.toString(par.hashCode()),
                            par.getWeighedEdgeListOwnerGraph()));
                }

                runNumber++;
            }
        }

        return topDir;
    }
}
