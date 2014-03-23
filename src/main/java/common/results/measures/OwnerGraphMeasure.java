package common.results.measures;

import common.results.CSVWriter;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Measure that returns a weighed edge list (in NCol format) of the ownership graph of a certain run.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class OwnerGraphMeasure extends Measure<String> {
    public OwnerGraphMeasure() {
        super("ownerGraph", null);
    }

    @Override
    public CSVWriter<String> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        // TODO for now just take first bin and first result
        Iterator<String> it = resultBins.keySet().iterator();
        Experiment.SimulationResult result = resultBins.get(it.next()).get(0);

        // Change some settings of the CSV writer
        csv.writeHeaders(false);
        csv.separator(" ");

        List<ReAuctionableParcel> pars = (List<ReAuctionableParcel>) result.simulationData;

        // Find parcel with most re-auctions, this will be the most interesting
        int maxReAuctions = Integer.MIN_VALUE;
        ReAuctionableParcel maxPar = null;
        for (ReAuctionableParcel par : pars) {
            if (par.getOwnerHistory().size() > maxReAuctions) {
                maxReAuctions = par.getOwnerHistory().size();
                maxPar = par;
            }
        }

        Map<String, Integer> edgeList = maxPar.getWeighedEdgeListOwnerGraph();

        // To CSV
        List<String> row;
        for (String edge : edgeList.keySet()) {
            row = new LinkedList<String>();
            row.add(edge.split("-")[0]);
            row.add(edge.split("-")[1]);
            row.add(Integer.toString(edgeList.get(edge)));

            csv.addRow(row);
        }

        return csv;
    }
}
