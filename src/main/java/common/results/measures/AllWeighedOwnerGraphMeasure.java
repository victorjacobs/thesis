package common.results.measures;

import common.results.Result;
import common.results.ResultDirectory;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is the same as {@link common.results.measures.WeighedOwnerGraphMeasure} except that it generates files for
 * every parcel in the simulation, neatly organised in directories.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class AllWeighedOwnerGraphMeasure extends OwnerGraphMeasure {
    @Override
    public Result<String> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        ResultDirectory<String> topDir = new ResultDirectory<String>("weighedGraph");
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
                    runDir.addResult(getWriter(Integer.toString(par.hashCode()), par.getWeighedEdgeListOwnerGraph()));
                }

                runNumber++;
            }
        }

        return topDir;
    }
}
