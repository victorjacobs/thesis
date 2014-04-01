package common.results.measures;

import common.results.CSVWriter;
import common.results.Result;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Measure that returns a weighed edge list (in NCol format) of the ownership graph of a certain run. Weight of a
 * certain edge is the number of times it occurs in the graph. This representation does not in any shape or form
 * retain order of the transitions.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class WeighedOwnerGraphMeasure extends OwnerGraphMeasure {
    public WeighedOwnerGraphMeasure() {
        super("weighedOwnerGraph");
    }

    @Override
    public Result<String> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        return getWriter(parcelToGenerateGraph(resultBins).getWeighedEdgeListOwnerGraph());
    }
}
