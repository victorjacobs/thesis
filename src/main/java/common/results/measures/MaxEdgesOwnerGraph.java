package common.results.measures;

import com.google.common.collect.Multimap;
import common.results.CSVWriter;
import common.results.Result;
import common.results.ResultDirectory;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.*;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Calculates the raw edge list (in NCol format) of the owner graph.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class MaxEdgesOwnerGraph extends Measure<String> {
    public MaxEdgesOwnerGraph() {
        super("maxEdgesGraphs", null);
    }

    @Override
    @SuppressWarnings("null")
    public Result evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        ResultDirectory<String> topDir = new ResultDirectory<String>(getName());
        ResultDirectory<String> configDir;

        int maxReAuctions;
        ReAuctionableParcel maxPar;

        for (String config : resultBins.keySet()) {
            configDir = new ResultDirectory<String>(config);
            topDir.addResult(configDir);

            maxReAuctions = Integer.MIN_VALUE;
            maxPar = null;

            for (Experiment.SimulationResult res : resultBins.get(config)) {
                for (ReAuctionableParcel par : getParcelsFromRun(res)) {
                    if (par.getOwnerHistory().size() > maxReAuctions) {
                        maxReAuctions = par.getOwnerHistory().size();
                        maxPar = par;
                    }
                }
            }

            configDir.addResult(getWriter("weighed", maxPar.getWeighedEdgeListOwnerGraph()));
            configDir.addResult(getWriter("regular", maxPar.getEdgeList()));
        }

        return topDir;
    }

    /**
     * Convert edge list to CSV file.
     *
     * @param edgeMap Map mapping edge to weight. If weight is -1, the weight is dropped.
     * @return Writer containing data from the graph
     */
    protected final CSVWriter<String> getWriter(String name, Multimap<String, Integer> edgeMap) {
        // Change some settings of the CSV writer
        CSVWriter<String> csv = new CSVWriter<String>(name);
        csv.writeHeaders(false);
        csv.separator(" ");

        // To CSV
        List<String> row;
        for (Map.Entry<String, Integer> edge : edgeMap.entries()) {
            row = new LinkedList<String>();
            row.add(edge.getKey().split("-")[0]);
            row.add(edge.getKey().split("-")[1]);
            if (edge.getValue() != -1)
                row.add(Integer.toString(edge.getValue()));

            csv.addRow(row);
        }

        return csv;
    }
}
