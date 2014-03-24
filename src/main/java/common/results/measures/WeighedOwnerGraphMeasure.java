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
public class WeighedOwnerGraphMeasure extends OwnerGraphMeasure {
    public WeighedOwnerGraphMeasure() {
        super("weighedOwnerGraph");
    }

    @Override
    public CSVWriter<String> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        return getWriter(parcelToProcess(resultBins).getWeighedEdgeListOwnerGraph());
    }
}
