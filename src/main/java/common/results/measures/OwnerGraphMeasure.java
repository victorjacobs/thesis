package common.results.measures;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import common.results.CSVWriter;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.*;

/**
 * Calculates the raw edge list (in NCol format) of the owner graph.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO this is broken because it uses a Map -> allows no double keys
public class OwnerGraphMeasure extends Measure<String> {
    public OwnerGraphMeasure() {
        super("ownerGraph", null);
    }

    protected OwnerGraphMeasure(String name) {
        super(name, null);
    }

    @Override
    public CSVWriter<String> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        return getWriter(parcelToProcess(resultBins).getEdgeList());
    }

    /**
     * Selection method used to pick a parcel to get the owner graph from. Right now it just takes the first run from
     * the first configuration.
     * TODO better selection method
     *
     * @param resultBins The binned experimental results
     * @return One parcel to calculate owner graph measures on
     */
    protected final ReAuctionableParcel parcelToProcess(Map<String, List<Experiment.SimulationResult>> resultBins) {
        Iterator<String> it = resultBins.keySet().iterator();
        Experiment.SimulationResult result = resultBins.get(it.next()).get(0);

        // Find parcel with most re-auctions, this will be the most interesting
        int maxReAuctions = Integer.MIN_VALUE;
        ReAuctionableParcel maxPar = null;
        for (ReAuctionableParcel par : getParcelsFromRun(result)) {
            if (par.getOwnerHistory().size() > maxReAuctions) {
                maxReAuctions = par.getOwnerHistory().size();
                maxPar = par;
            }
        }

        return maxPar;
    }

    /**
     * Convert edge list to CSV file.
     *
     * @param edgeMap Map mapping edge to weight. If weight is -1, the weight is dropped.
     * @return Writer containing data from the graph
     */
    protected final CSVWriter<String> getWriter(Multimap<String, Integer> edgeMap) {
        // Change some settings of the CSV writer
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
